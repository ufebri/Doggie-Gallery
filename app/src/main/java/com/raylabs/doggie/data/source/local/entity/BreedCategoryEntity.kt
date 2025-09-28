package com.raylabs.doggie.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breed_categories",
    indices = [Index(value = ["displayName"])]
)
data class BreedCategoryEntity(
    @PrimaryKey val id: String,
    val breed: String,
    val subBreed: String?,
    val displayName: String,
    val previewImageUrl: String?,
    val lastRefreshTimestamp: Long
) {
    companion object {
        fun createId(breed: String, subBreed: String?): String =
            if (subBreed.isNullOrBlank()) breed else "$breed-$subBreed"
    }
}
