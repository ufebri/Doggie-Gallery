package com.raylabs.doggie.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.raylabs.doggie.data.source.local.BreedCategoryLocalDataSource
import com.raylabs.doggie.data.source.local.entity.BreedCategoryEntity
import com.raylabs.doggie.data.source.remote.RemoteDataSource
import com.raylabs.doggie.vo.BreedCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class BreedCategoryRepositoryHelper(
    private val context: Context,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: BreedCategoryLocalDataSource,
    private val pagingConfig: PagingConfig,
) {

    private val seedingStarted = AtomicBoolean(false)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val inFlightPreviewRequests =
        Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())
    private val previewSemaphore = Semaphore(permits = 3)

    fun pager(): Flow<PagingData<BreedCategory>> {
        ensureSeeded()
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { localDataSource.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    fun catalogFlow(): Flow<List<BreedCategory>> {
        ensureSeeded()
        return localDataSource.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
    }

    fun requestPreview(category: BreedCategory) {
        val id = BreedCategoryEntity.createId(category.breed, category.subBreed)
        if (!inFlightPreviewRequests.add(id)) {
            return
        }
        scope.launch {
            previewSemaphore.withPermit {
                try {
                    val current = localDataSource.findById(id)
                    if (current != null && !current.previewImageUrl.isNullOrBlank()) {
                        return@withPermit
                    }
                    val newUrl = fetchPreviewUrl(category.breed, category.subBreed)
                    if (!newUrl.isNullOrBlank() && newUrl != current?.previewImageUrl) {
                        localDataSource.updatePreview(id, newUrl, System.currentTimeMillis())
                    }
                } finally {
                    inFlightPreviewRequests.remove(id)
                }
            }
        }
    }

    fun scheduleBackgroundSync() {
        runCatching { CategoryPreviewSyncWorker.schedule(context) }
    }

    suspend fun refreshOldestPreviews(batchSize: Int) {
        ensureSeeded()
        syncRemoteCatalog(force = false)
        val targets = localDataSource.oldestEntries(batchSize)
        targets.forEach { entity ->
            val newUrl = fetchPreviewUrl(entity.breed, entity.subBreed)
            localDataSource.updatePreview(entity.id, newUrl, System.currentTimeMillis())
        }
    }

    private fun ensureSeeded() {
        if (!seedingStarted.compareAndSet(false, true)) {
            return
        }
        scope.launch {
            val hasData = localDataSource.count() > 0
            syncRemoteCatalog(force = !hasData)
        }
    }

    private suspend fun fetchPreviewUrl(breed: String, subBreed: String?): String? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.getRandomImagesSync(breed, subBreed, 1).firstOrNull()
        }
    }

    private fun createEntity(
        breed: String,
        subBreed: String?,
        timestamp: Long,
        preview: String? = null
    ): BreedCategoryEntity {
        val id = BreedCategoryEntity.createId(breed, subBreed)
        return BreedCategoryEntity(
            id = id,
            breed = breed,
            subBreed = subBreed,
            displayName = formatDisplayName(breed, subBreed),
            previewImageUrl = preview,
            lastRefreshTimestamp = timestamp
        )
    }

    private suspend fun syncRemoteCatalog(force: Boolean) {
        val remote = withContext(Dispatchers.IO) {
            remoteDataSource.getAllBreedsSync()
        }
        if (remote.isEmpty()) {
            return
        }
        val timestamp = System.currentTimeMillis()
        val mapped = remote.entries
            .flatMap { (breed, subBreeds) ->
                if (subBreeds.isNullOrEmpty()) {
                    listOf(createEntity(breed, null, timestamp))
                } else {
                    subBreeds.map { sub -> createEntity(breed, sub, timestamp) }
                }
            }
        if (force) {
            localDataSource.upsertAll(mapped)
        } else {
            val existingIds = localDataSource.allIds().toSet()
            val newOnes = mapped.filterNot { existingIds.contains(it.id) }
            if (newOnes.isNotEmpty()) {
                val adjusted = newOnes.map { it.copy(lastRefreshTimestamp = 0L) }
                localDataSource.upsertAll(adjusted)
            }
        }
    }

    private fun BreedCategoryEntity.toDomain(): BreedCategory =
        BreedCategory(
            breed = breed,
            subBreed = subBreed,
            displayName = displayName,
            imageUrl = previewImageUrl.orEmpty()
        )

    private fun formatDisplayName(breed: String, subBreed: String?): String {
        val locale = Locale.getDefault()
        val capitalizedBreed =
            breed.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        return if (subBreed.isNullOrBlank()) {
            capitalizedBreed
        } else {
            val capitalizedSub =
                subBreed.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            "$capitalizedSub $capitalizedBreed"
        }
    }
}
