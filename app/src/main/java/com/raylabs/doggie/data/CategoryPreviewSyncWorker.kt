package com.raylabs.doggie.data

import android.content.Context
import androidx.paging.PagingConfig
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.raylabs.doggie.data.source.local.BreedCategoryLocalDataSource
import com.raylabs.doggie.data.source.local.room.DoggieDatabase
import com.raylabs.doggie.data.source.remote.RemoteDataSource
import java.util.concurrent.TimeUnit

class CategoryPreviewSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = DoggieDatabase.getInstance(applicationContext)
        val localDataSource = BreedCategoryLocalDataSource(database.breedCategoryDao())
        val remoteDataSource = RemoteDataSource.getInstance()
        val helper = BreedCategoryRepositoryHelper(
            context = applicationContext,
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            pagingConfig = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                initialLoadSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            )
        )

        return runCatching {
            helper.refreshOldestPreviews(BATCH_SIZE)
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_PERIODIC_NAME = "category_preview_sync_periodic"
        private const val UNIQUE_ONE_OFF_NAME = "category_preview_sync_one_off"
        private const val BATCH_SIZE = 20
        private const val DEFAULT_PAGE_SIZE = 20
        private val CONSTRAINTS = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun schedule(context: Context) {
            runCatching {
                val workManager = WorkManager.getInstance(context)

                val periodicRequest =
                    PeriodicWorkRequestBuilder<CategoryPreviewSyncWorker>(3, TimeUnit.DAYS)
                        .setConstraints(CONSTRAINTS)
                        .build()

                workManager.enqueueUniquePeriodicWork(
                    UNIQUE_PERIODIC_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicRequest
                )

                val oneOffRequest = OneTimeWorkRequestBuilder<CategoryPreviewSyncWorker>()
                    .setConstraints(CONSTRAINTS)
                    .build()

                workManager.enqueueUniqueWork(
                    UNIQUE_ONE_OFF_NAME,
                    ExistingWorkPolicy.KEEP,
                    oneOffRequest
                )
            }
        }
    }
}
