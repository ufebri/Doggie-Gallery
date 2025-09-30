package com.raylabs.doggie.data.source.local

import androidx.paging.PagingSource
import com.raylabs.doggie.data.source.local.entity.BreedCategoryEntity
import com.raylabs.doggie.data.source.local.room.BreedCategoryDao
import kotlinx.coroutines.flow.Flow

class BreedCategoryLocalDataSource(private val dao: BreedCategoryDao) {

    fun pagingSource(): PagingSource<Int, BreedCategoryEntity> = dao.pagingSource()

    suspend fun count(): Int = dao.count()

    suspend fun upsertAll(categories: List<BreedCategoryEntity>) = dao.upsertAll(categories)

    suspend fun findById(id: String): BreedCategoryEntity? = dao.findById(id)

    suspend fun allIds(): List<String> = dao.allIds()

    fun observeAll(): Flow<List<BreedCategoryEntity>> = dao.observeAll()

    suspend fun updatePreview(id: String, url: String?, updatedAt: Long) =
        dao.updatePreview(id, url, updatedAt)

    suspend fun oldestEntries(limit: Int): List<BreedCategoryEntity> = dao.oldestEntries(limit)
}
