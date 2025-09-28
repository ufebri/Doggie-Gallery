package com.raylabs.doggie.data.source.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raylabs.doggie.data.source.local.entity.BreedCategoryEntity

@Dao
interface BreedCategoryDao {

    @Query("SELECT * FROM breed_categories ORDER BY displayName")
    fun pagingSource(): PagingSource<Int, BreedCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<BreedCategoryEntity>)

    @Query("SELECT COUNT(*) FROM breed_categories")
    suspend fun count(): Int

    @Query("SELECT * FROM breed_categories WHERE id = :id")
    suspend fun findById(id: String): BreedCategoryEntity?

    @Query("SELECT id FROM breed_categories")
    suspend fun allIds(): List<String>

    @Query(
        "UPDATE breed_categories SET previewImageUrl = :previewUrl, lastRefreshTimestamp = :updatedAt WHERE id = :id"
    )
    suspend fun updatePreview(id: String, previewUrl: String?, updatedAt: Long)

    @Query("SELECT * FROM breed_categories ORDER BY lastRefreshTimestamp ASC LIMIT :limit")
    suspend fun oldestEntries(limit: Int): List<BreedCategoryEntity>
}
