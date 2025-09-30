package com.raylabs.doggie.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.raylabs.doggie.data.source.local.BreedCategoryLocalDataSource
import com.raylabs.doggie.data.source.local.LocalDataSource
import com.raylabs.doggie.data.source.local.entity.DoggieEntity
import com.raylabs.doggie.data.source.remote.ApiResponse
import com.raylabs.doggie.data.source.remote.RemoteDataSource
import com.raylabs.doggie.utils.AppExecutors
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.check
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.Executor

class DoggieRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Gunakan executors synchronous supaya LiveData & background task jalan inline
    private fun directAppExecutors(): AppExecutors {
        val direct = Executor { it.run() }
        return AppExecutors(direct, direct, direct)
    }

    private fun newRepository(
        context: Context,
        remote: RemoteDataSource,
        local: LocalDataSource,
        breedLocal: BreedCategoryLocalDataSource
    ): DoggieRepository {
        // DoggieRepository adalah singleton; reset supaya tidak kebawa antar test.
        DoggieRepository.resetForTesting()

        whenever(context.applicationContext).thenReturn(context)

        return DoggieRepository.getInstance(
            context,
            remote,
            local,
            breedLocal,
            directAppExecutors()
        )
    }

    @After
    fun tearDown() {
        DoggieRepository.resetForTesting()
    }

    @Test
    fun `getAllImage - DB empty triggers fetch, saves with category for-you`() {
        val context = mock<Context>()
        val remote = mock<RemoteDataSource>()
        val local = mock<LocalDataSource>()
        val breedLocal = mock<BreedCategoryLocalDataSource>()

        // DB kosong -> shouldFetch = true
        whenever(local.getAllDoggie(eq("for-you"))).thenReturn(MutableLiveData(emptyList()))

        // Remote sukses
        val urls = listOf(
            "https://images.dog.ceo/breeds/pug/abc.jpg",                  // split()[4] = "pug"
            "https://images.dog.ceo/breeds/hound-afghan/xyz.jpg"          // split()[4] = "hound-afghan"
        )
        val live = MutableLiveData(ApiResponse.success(urls))
        whenever(remote.getAllImage(any())).thenReturn(live)

        val repo = newRepository(context, remote, local, breedLocal)

        // Observe hasilnya agar NetworkBoundResource berjalan
        val result = repo.getAllImage("2")
        result.observeForever { /* no-op */ }

        // Verifikasi insertDoggie dipanggil dengan transformasi yang benar
        val listCaptor = argumentCaptor<ArrayList<DoggieEntity>>()
        verify(local).insertDoggie(listCaptor.capture())

        val saved = listCaptor.firstValue
        assert(saved.size == 2)
        // record 1
        assert(saved[0].type == "pug")
        assert(saved[0].tag == "for-you")
        assert(saved[0].link == "https://images.dog.ceo/breeds/pug/abc.jpg")
        // record 2
        assert(saved[1].type == "hound-afghan")
        assert(saved[1].tag == "for-you")
        assert(saved[1].link == "https://images.dog.ceo/breeds/hound-afghan/xyz.jpg")
    }

    @Test
    fun `getLikedImage - DB empty triggers fetch, saves with category liked`() {
        val context = mock<Context>()
        val remote = mock<RemoteDataSource>()
        val local = mock<LocalDataSource>()
        val breedLocal = mock<BreedCategoryLocalDataSource>()

        whenever(local.getAllDoggie(eq("liked"))).thenReturn(MutableLiveData(emptyList()))

        val urls = listOf(
            "https://images.dog.ceo/breeds/shiba/1.jpg",
            "https://images.dog.ceo/breeds/bulldog-french/2.jpg"
        )
        val live = MutableLiveData(ApiResponse.success(urls))
        whenever(remote.getAllImage(any())).thenReturn(live)

        val repo = newRepository(context, remote, local, breedLocal)
        repo.getLikedImage("2").observeForever { }

        val listCaptor = argumentCaptor<ArrayList<DoggieEntity>>()
        verify(local).insertDoggie(listCaptor.capture())

        val saved = listCaptor.firstValue
        assert(saved.size == 2)
        assert(saved[0].type == "shiba" && saved[0].tag == "liked")
        assert(saved[1].type == "bulldog-french" && saved[1].tag == "liked")
    }

    @Test
    fun `getPopularImage - DB empty triggers fetch, saves with category popular`() {
        val context = mock<Context>()
        val remote = mock<RemoteDataSource>()
        val local = mock<LocalDataSource>()
        val breedLocal = mock<BreedCategoryLocalDataSource>()

        whenever(local.getAllDoggie(eq("popular"))).thenReturn(MutableLiveData(emptyList()))

        val urls = listOf(
            "https://images.dog.ceo/breeds/beagle/a.jpg",
            "https://images.dog.ceo/breeds/husky/b.jpg"
        )
        val live = MutableLiveData(ApiResponse.success(urls))
        whenever(remote.getAllImage(any())).thenReturn(live)

        val repo = newRepository(context, remote, local, breedLocal)
        repo.getPopularImage("2").observeForever { }

        verify(local).insertDoggie(
            check<ArrayList<DoggieEntity>> { saved ->
                assert(saved.size == 2)
                assert(saved[0].type == "beagle")
                assert(saved[0].tag == "popular")
                assert(saved[1].type == "husky")
                assert(saved[1].tag == "popular")
            }
        )
    }
}
