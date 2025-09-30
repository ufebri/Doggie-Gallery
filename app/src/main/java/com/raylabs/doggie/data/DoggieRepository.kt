package com.raylabs.doggie.data

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.raylabs.doggie.data.paging.BreedImagesPagingSource
import com.raylabs.doggie.data.source.local.BreedCategoryLocalDataSource
import com.raylabs.doggie.data.source.local.LocalDataSource
import com.raylabs.doggie.data.source.local.entity.DoggieEntity
import com.raylabs.doggie.data.source.remote.ApiResponse
import com.raylabs.doggie.data.source.remote.RemoteDataSource
import com.raylabs.doggie.utils.AppExecutors
import com.raylabs.doggie.vo.BreedCategory
import com.raylabs.doggie.vo.Resource
import kotlinx.coroutines.flow.Flow

class DoggieRepository private constructor(
    context: Context,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    breedCategoryLocalDataSource: BreedCategoryLocalDataSource,
    private val appExecutors: AppExecutors
) : DoggieDataSource {

    private val breedCategoryHelper = BreedCategoryRepositoryHelper(
        context,
        remoteDataSource,
        breedCategoryLocalDataSource,
        PagingConfig(CATEGORY_PAGE_SIZE, CATEGORY_PAGE_SIZE, false)
    )

    init {
        breedCategoryHelper.scheduleBackgroundSync()
    }

    override fun getAllImage(countItem: String): LiveData<Resource<List<DoggieEntity>>> {
        return object : NetworkBoundResource<List<DoggieEntity>, List<String>>(appExecutors) {
            override fun loadFromDB(): LiveData<List<DoggieEntity>> =
                localDataSource.getAllDoggie(FOR_YOU_TAG)

            override fun shouldFetch(data: List<DoggieEntity>?): Boolean = data.isNullOrEmpty()

            override fun createCall(): LiveData<ApiResponse<List<String>>> =
                remoteDataSource.getAllImage(countItem)

            override fun saveCallResult(data: List<String>) {
                val doggies = data.mapNotNull { link ->
                    val parts = link.split('/')
                    val type = parts.getOrNull(4) ?: return@mapNotNull null
                    DoggieEntity(type, link, FOR_YOU_TAG)
                }
                if (doggies.isNotEmpty()) {
                    localDataSource.insertDoggie(doggies)
                }
            }
        }.asLiveData()
    }

    override fun getLikedImage(countItem: String): LiveData<Resource<List<DoggieEntity>>> {
        return object : NetworkBoundResource<List<DoggieEntity>, List<String>>(appExecutors) {
            override fun loadFromDB(): LiveData<List<DoggieEntity>> =
                localDataSource.getAllDoggie(LIKED_TAG)

            override fun shouldFetch(data: List<DoggieEntity>?): Boolean = data.isNullOrEmpty()

            override fun createCall(): LiveData<ApiResponse<List<String>>> =
                remoteDataSource.getAllImage(countItem)

            override fun saveCallResult(data: List<String>) {
                val doggies = data.mapNotNull { link ->
                    val parts = link.split('/')
                    val type = parts.getOrNull(4) ?: return@mapNotNull null
                    DoggieEntity(type, link, LIKED_TAG)
                }
                if (doggies.isNotEmpty()) {
                    localDataSource.insertDoggie(doggies)
                }
            }
        }.asLiveData()
    }

    override fun getPopularImage(countItem: String): LiveData<Resource<List<DoggieEntity>>> {
        return object : NetworkBoundResource<List<DoggieEntity>, List<String>>(appExecutors) {
            override fun loadFromDB(): LiveData<List<DoggieEntity>> =
                localDataSource.getAllDoggie(POPULAR_TAG)

            override fun shouldFetch(data: List<DoggieEntity>?): Boolean = data.isNullOrEmpty()

            override fun createCall(): LiveData<ApiResponse<List<String>>> =
                remoteDataSource.getAllImage(countItem)

            override fun saveCallResult(data: List<String>) {
                val doggies = data.mapNotNull { link ->
                    val parts = link.split('/')
                    val type = parts.getOrNull(4) ?: return@mapNotNull null
                    DoggieEntity(type, link, POPULAR_TAG)
                }
                if (doggies.isNotEmpty()) {
                    localDataSource.insertDoggie(doggies)
                }
            }
        }.asLiveData()
    }

    override fun getCategories(): Flow<PagingData<BreedCategory>> = breedCategoryHelper.pager()

    override fun requestCategoryPreview(category: BreedCategory) {
        breedCategoryHelper.requestPreview(category)
    }

    override fun getBreedImages(
        breed: String,
        subBreed: String?,
        pageSize: Int
    ): Flow<PagingData<String>> {
        val effectivePageSize = pageSize.takeIf { it > 0 } ?: DEFAULT_BREED_IMAGE_PAGE_SIZE
        return Pager(
            PagingConfig(
                pageSize = effectivePageSize,
                initialLoadSize = effectivePageSize,
                enablePlaceholders = false
            )
        ) {
            BreedImagesPagingSource(remoteDataSource, breed, subBreed, effectivePageSize)
        }.flow
    }

    override fun observeBreedCatalog(): Flow<List<BreedCategory>> =
        breedCategoryHelper.catalogFlow()

    companion object {
        private const val CATEGORY_PAGE_SIZE = 20
        private const val DEFAULT_BREED_IMAGE_PAGE_SIZE = 10
        private const val FOR_YOU_TAG = "for-you"
        private const val LIKED_TAG = "liked"
        private const val POPULAR_TAG = "popular"

        @Volatile
        private var INSTANCE: DoggieRepository? = null

        @JvmStatic
        fun getInstance(
            context: Context,
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource,
            breedCategoryLocalDataSource: BreedCategoryLocalDataSource,
            appExecutors: AppExecutors
        ): DoggieRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DoggieRepository(
                    context.applicationContext,
                    remoteDataSource,
                    localDataSource,
                    breedCategoryLocalDataSource,
                    appExecutors
                ).also { INSTANCE = it }
            }
        }

        @VisibleForTesting
        @JvmStatic
        fun resetForTesting() {
            INSTANCE = null
        }
    }
}
