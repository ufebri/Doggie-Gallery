package com.raylabs.doggie.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.raylabs.doggie.data.source.remote.RemoteDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BreedImagesPagingSourceMockitoTest {

    private fun newSut(
        remote: RemoteDataSource,
        breed: String = "hound",
        subBreed: String? = "afghan",
        pageSize: Int = 3
    ) = BreedImagesPagingSource(
        remoteDataSource = remote,
        breed = breed,
        subBreed = subBreed,
        pageSize = pageSize
    )

    @Test
    fun `load first page success - returns data, prevKey null, nextKey +1`() = runTest {
        val remote = org.mockito.kotlin.mock<RemoteDataSource>()
        whenever(remote.getRandomImagesSync(eq("hound"), eq("afghan"), eq(3)))
            .thenReturn(listOf("u1", "u2", "u3"))

        val sut = newSut(remote, pageSize = 3)

        val result = sut.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertEquals(listOf("u1", "u2", "u3"), result.data)
        assertNull(result.prevKey)
        assertEquals(1, result.nextKey)

        // Dipanggil tepat 1 kali
        verify(remote, times(1)).getRandomImagesSync("hound", "afghan", 3)
    }

    @Test
    fun `deduplicate across attempts - no duplicates and at least pageSize`() = runTest {
        val remote = org.mockito.kotlin.mock<RemoteDataSource>()
        // attempt 1
        whenever(remote.getRandomImagesSync(eq("bulldog"), anyOrNull(), eq(4)))
            .thenReturn(listOf("a", "a", "b", "c"))
            // attempt 2
            .thenReturn(listOf("b", "d", "e"))

        val sut = newSut(remote, breed = "bulldog", subBreed = null, pageSize = 4)

        val page = sut.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 4,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        val data = page.data
        assertTrue(data.size >= 4)
        assertEquals(data.size, data.toSet().size)
        assertTrue(data.containsAll(listOf("a", "b", "c", "d")))

        verify(remote, times(2)).getRandomImagesSync("bulldog", null, 4)
    }

    @Test
    fun `stop when remote returns empty - empty page with nextKey null`() = runTest {
        val remote = org.mockito.kotlin.mock<RemoteDataSource>()
        whenever(remote.getRandomImagesSync(eq("pug"), anyOrNull(), eq(3)))
            .thenReturn(emptyList())

        val sut = newSut(remote, breed = "pug", subBreed = null, pageSize = 3)

        val page = sut.load(
            PagingSource.LoadParams.Refresh(
                key = null, loadSize = 3, placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)

        verify(remote, times(1)).getRandomImagesSync("pug", null, 3)
    }

    @Test
    fun `propagate error as LoadResult_Error`() = runTest {
        val remote = org.mockito.kotlin.mock<RemoteDataSource>()
        whenever(remote.getRandomImagesSync(eq("husky"), anyOrNull(), eq(2)))
            .thenThrow(IllegalStateException("boom"))

        val sut = newSut(remote, breed = "husky", subBreed = null, pageSize = 2)

        val result = sut.load(
            PagingSource.LoadParams.Refresh(
                key = null, loadSize = 2, placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Error

        assertTrue(result.throwable is IllegalStateException)
        assertEquals("boom", result.throwable.message)

        verify(remote, times(1)).getRandomImagesSync("husky", null, 2)
    }

    @Test
    fun `respect maxFetchAttempts - stops after 5 attempts if still under pageSize`() = runTest {
        val pageSize = 10
        val remote = org.mockito.kotlin.mock<RemoteDataSource>()

        // Simulasikan 5 attempt; setiap kali hanya memberi 1 URL unik
        whenever(remote.getRandomImagesSync(eq("beagle"), anyOrNull(), eq(pageSize)))
            .thenReturn(listOf("u1"))
            .thenReturn(listOf("u2"))
            .thenReturn(listOf("u3"))
            .thenReturn(listOf("u4"))
            .thenReturn(listOf("u5"))
        // attempt ke-6 tidak pernah dipanggil (maxFetchAttempts = 5)

        val sut = newSut(remote, breed = "beagle", subBreed = null, pageSize = pageSize)

        val page = sut.load(
            PagingSource.LoadParams.Refresh(
                key = null, loadSize = pageSize, placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page

        // Hanya 5 item, karena 5 attempt * 1 unik, lalu berhenti.
        assertEquals(5, page.data.size)
        assertEquals(1, page.nextKey)

        verify(remote, times(5)).getRandomImagesSync("beagle", null, pageSize)
    }

    @Test
    fun `getRefreshKey - prevKey plus 1`() {
        val pages = listOf(
            PagingSource.LoadResult.Page(
                data = listOf("p0a", "p0b", "p0c"),
                prevKey = null,
                nextKey = 1
            ),
            PagingSource.LoadResult.Page(
                data = listOf("p1a", "p1b", "p1c"),
                prevKey = 0,
                nextKey = 2
            )
        )

        val state = PagingState(
            pages = pages,
            anchorPosition = 4, // berada pada page indeks 1
            config = PagingConfig(pageSize = 3),
            leadingPlaceholderCount = 0
        )

        val sut = newSut(org.mockito.kotlin.mock())
        val key = sut.getRefreshKey(state)
        assertEquals(1, key)
    }

    @Test
    fun `getRefreshKey - fallback nextKey minus 1 when prevKey null`() {
        val pages = listOf(
            PagingSource.LoadResult.Page(
                data = listOf("x1", "x2"),
                prevKey = null,
                nextKey = 5
            )
        )
        val state = PagingState(
            pages = pages,
            anchorPosition = 1,
            config = PagingConfig(pageSize = 2),
            leadingPlaceholderCount = 0
        )

        val sut = newSut(org.mockito.kotlin.mock())
        val key = sut.getRefreshKey(state)
        assertEquals(4, key)
    }
}