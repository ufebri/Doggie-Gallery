package com.raylabs.doggie.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.raylabs.doggie.data.source.remote.RemoteDataSource
import com.raylabs.doggie.vo.BreedCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class CategoriesPagingSource(
    private val remoteDataSource: RemoteDataSource,
    private val pageSize: Int // pageSize mungkin tidak terlalu relevan jika kita load semua kategori sekali
) : PagingSource<Int, BreedCategory>() {

    // Cache untuk gambar mungkin masih berguna jika Anda ingin mengambil gambar pratinjau di sini
    // Namun, untuk Paging 3, biasanya gambar dimuat oleh adapter saat item ditampilkan.
    // Untuk kesederhanaan awal, saya akan menghapus imageCache dari PagingSource ini.
    // Jika Anda ingin mempertahankan pemuatan gambar acak untuk setiap kategori di PagingSource,
    // itu akan menjadi lebih kompleks dan setiap panggilan itu juga harus di background thread.

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BreedCategory> {
        val currentPage = params.key ?: 1 // Kunci bisa null untuk pemuatan awal

        return try {
            if (currentPage == 1) { // Hanya muat data untuk "halaman" pertama
                // Jalankan operasi jaringan di background thread
                val rawCategoriesMap = withContext(Dispatchers.IO) {
                    remoteDataSource.getAllBreedsSync()
                }

                if (rawCategoriesMap.isNullOrEmpty()) {
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null // Tidak ada halaman berikutnya jika data kosong
                    )
                }

                // Logika konversi dari Map ke List<BreedCategory> (mirip dengan yang ada di Repository)
                val categoriesList = rawCategoriesMap.entries
                    .flatMap { (breed, subBreeds) ->
                        if (subBreeds.isNullOrEmpty()) {
                            listOf(BreedCategory(breed, null, capitalize(breed), ""))
                        } else {
                            subBreeds.map { sub ->
                                BreedCategory(breed, sub, formatSubBreed(sub, breed), "")
                            }
                        }
                    }
                    .sortedBy { it.displayName.lowercase(Locale.getDefault()) }

                // Jika Anda masih ingin memuat gambar pratinjau acak di sini:
                // Ini akan memperlambat pemuatan awal karena banyak panggilan jaringan.
                // Setiap panggilan remoteDataSource.getRandomImagesSync juga harus dalam withContext(Dispatchers.IO)
                // Saya akan mengomentari bagian ini untuk saat ini agar fokus pada paging kategori dasar.
                /*
                val categoriesWithImages = categoriesList.map { category ->
                    val imageUrl = withContext(Dispatchers.IO) {
                        remoteDataSource.getRandomImagesSync(category.breed, category.subBreed, 1)
                            .firstOrNull().orEmpty()
                    }
                    category.copy(imageUrl = imageUrl)
                }
                */

                LoadResult.Page(
                    data = categoriesList, // atau categoriesWithImages jika Anda mengaktifkan kode di atas
                    prevKey = null, // Tidak ada halaman sebelumnya untuk pemuatan awal
                    nextKey = null  // Set null karena kita memuat semua kategori sekaligus.
                    // Jika Anda ingin membagi categoriesList menjadi beberapa halaman secara manual,
                    // maka Anda perlu logika nextKey dan pageSize di sini.
                )
            } else {
                // Untuk halaman > 1, kembalikan daftar kosong karena kita sudah memuat semuanya
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null, // Atau currentPage -1 jika Anda punya logika paging manual
                    nextKey = null
                )
            }
        } catch (throwable: Throwable) {
            LoadResult.Error(throwable)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, BreedCategory>): Int? {
        // Coba temukan halaman terdekat dengan posisi anchor
        // Jika anchorPosition null, artinya data belum dimuat, jadi kita mulai dari awal (null akan jadi 1 di load())
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun capitalize(value: String): String =
        value.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        }

    private fun formatSubBreed(subBreed: String, breed: String): String =
        "${capitalize(subBreed)} ${capitalize(breed)}"
}
