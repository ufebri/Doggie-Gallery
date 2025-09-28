package com.raylabs.doggie.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.raylabs.doggie.data.source.remote.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BreedImagesPagingSource(
    private val remoteDataSource: RemoteDataSource,
    private val breed: String,
    private val subBreed: String?,
    private val pageSize: Int
) : PagingSource<Int, String>() {

    private val emittedUrls = mutableSetOf<String>()
    private val maxFetchAttempts = 5

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        // Halaman saat ini yang akan dimuat. Jika params.key null, kita mulai dari halaman 0 (atau 1 jika Anda prefer).
        // Untuk API dog.ceo yang tidak memiliki cursor/offset, 'key' adalah nomor halaman simulasi kita.
        val currentPageIndex = params.key ?: 0 // Atau FIRST_PAGE_INDEX jika Anda punya konstanta

        return try {
            val pageData = mutableListOf<String>()
            var attempt = 0

            while (pageData.size < pageSize && attempt < maxFetchAttempts) {
                val batch = withContext(Dispatchers.IO) {
                    remoteDataSource.getRandomImagesSync(breed, subBreed, pageSize)
                }
                if (batch.isEmpty()) {
                    break
                }
                val unique = batch.filter { emittedUrls.add(it) }
                pageData.addAll(unique)
                attempt++
            }

            // Jika API mengembalikan daftar kosong (misalnya, tidak ada gambar untuk breed tersebut, atau akhir dari data jika API mendukungnya)
            // Atau jika kita ingin berhenti memuat setelah sejumlah halaman tertentu (bisa ditambahkan logika di sini)
            if (pageData.isEmpty()) {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (currentPageIndex == 0) null else currentPageIndex - 1,
                    nextKey = null // Tidak ada halaman berikutnya jika tidak ada gambar
                )
            } else {
                LoadResult.Page(
                    data = pageData,
                    prevKey = if (currentPageIndex == 0) null else currentPageIndex - 1,
                    // nextKey akan menjadi halaman berikutnya.
                    // Jika Anda ingin membatasi jumlah halaman (misalnya MAX_PAGE seperti sebelumnya),
                    // Anda bisa menambahkan kondisi di sini:
                    // nextKey = if (currentPageIndex >= MAX_PAGE_SIMULATION - 1) null else currentPageIndex + 1
                    nextKey = currentPageIndex + 1
                )
            }
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
        // Coba temukan halaman terdekat dengan posisi anchor.
        // Ini membantu Paging library untuk memutuskan dari mana memulai pemuatan ulang jika data di-refresh.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // companion object {
    //     private const val FIRST_PAGE_INDEX = 0 // Atau 1, sesuaikan dengan preferensi Anda
    //     // private const val MAX_PAGE_SIMULATION = 3 // Jika Anda ingin membatasi
    // }
}
