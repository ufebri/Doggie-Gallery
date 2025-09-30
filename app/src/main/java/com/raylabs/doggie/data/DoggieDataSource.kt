package com.raylabs.doggie.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.raylabs.doggie.data.source.local.entity.DoggieEntity
import com.raylabs.doggie.vo.BreedCategory
import com.raylabs.doggie.vo.Resource
import kotlinx.coroutines.flow.Flow

interface DoggieDataSource {
    fun getAllImage(countItem: String): LiveData<Resource<List<DoggieEntity>>>

    fun getLikedImage(countItem: String): LiveData<Resource<List<DoggieEntity>>>

    fun getPopularImage(countItem: String): LiveData<Resource<List<DoggieEntity>>>

    fun getCategories(): Flow<PagingData<BreedCategory>>

    fun requestCategoryPreview(category: BreedCategory)

    fun getBreedImages(breed: String, subBreed: String?, pageSize: Int): Flow<PagingData<String>>

    fun observeBreedCatalog(): Flow<List<BreedCategory>>
}
