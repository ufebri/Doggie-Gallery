package com.raylabs.doggie.data.source.local

import androidx.paging.PagingSource
import com.raylabs.doggie.data.source.local.entity.BreedCategoryEntity
import com.raylabs.doggie.data.source.local.room.BreedCategoryDao
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class BreedCategoryLocalDataSourceTest {

    private val dao = mock<BreedCategoryDao>()
    private val sut = BreedCategoryLocalDataSource(dao)

    @Test
    fun `pagingSource delegates to dao`() {
        val pagingSource = mock<PagingSource<Int, BreedCategoryEntity>>()
        whenever(dao.pagingSource()).thenReturn(pagingSource)

        val result = sut.pagingSource()

        assert(result === pagingSource)
        verify(dao).pagingSource()
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `count delegates to dao`() = runTest {
        whenever(dao.count()).thenReturn(42)

        val result = sut.count()

        assert(result == 42)
        verify(dao).count()
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `upsertAll delegates to dao`() = runTest {
        val list = listOf(
            BreedCategoryEntity(
                id = "hound-afghan",
                breed = "hound",
                subBreed = "afghan",
                displayName = "Afghan Hound",
                previewImageUrl = null,
                lastRefreshTimestamp = 0L
            )
        )

        sut.upsertAll(list)

        verify(dao).upsertAll(list)
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `findById delegates to dao`() = runTest {
        val entity = BreedCategoryEntity(
            id = "pug", breed = "pug", subBreed = null,
            displayName = "Pug", previewImageUrl = "u", lastRefreshTimestamp = 1L
        )
        whenever(dao.findById("pug")).thenReturn(entity)

        val result = sut.findById("pug")

        assert(result === entity)
        verify(dao).findById("pug")
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `allIds delegates to dao`() = runTest {
        whenever(dao.allIds()).thenReturn(listOf("pug", "hound-afghan"))

        val result = sut.allIds()

        assert(result == listOf("pug", "hound-afghan"))
        verify(dao).allIds()
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `updatePreview delegates to dao`() = runTest {
        sut.updatePreview("pug", "url", 123L)

        verify(dao).updatePreview("pug", "url", 123L)
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun `oldestEntries delegates to dao`() = runTest {
        val e = BreedCategoryEntity(
            id = "pug", breed = "pug", subBreed = null,
            displayName = "Pug", previewImageUrl = null, lastRefreshTimestamp = 0L
        )
        whenever(dao.oldestEntries(5)).thenReturn(listOf(e))

        val result = sut.oldestEntries(5)

        assert(result == listOf(e))
        verify(dao).oldestEntries(5)
        verifyNoMoreInteractions(dao)
    }
}