package com.raylabs.doggie.data

import android.content.Context
import androidx.paging.PagingConfig
import com.raylabs.doggie.data.source.local.BreedCategoryLocalDataSource
import com.raylabs.doggie.data.source.local.entity.BreedCategoryEntity
import com.raylabs.doggie.data.source.remote.RemoteDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BreedCategoryRepositoryHelperTest {

    private lateinit var context: Context
    private lateinit var remote: RemoteDataSource
    private lateinit var local: BreedCategoryLocalDataSource

    private lateinit var repo: BreedCategoryRepositoryHelper

    @Before
    fun setup() {
        context = mock()
        remote = mock()
        local = mock()

        val pagingConfig = PagingConfig(pageSize = 10)
        repo = BreedCategoryRepositoryHelper(
            context = context,
            remoteDataSource = remote,
            localDataSource = local,
            pagingConfig = pagingConfig
        )
    }

    @Test
    fun `refreshOldestPreviews - inserts new categories with lastRefreshTimestamp set to 0 when not forcing`() =
        runTest {
            // GIVEN: local sudah punya data -> ensureSeeded() akan memanggil syncRemoteCatalog(force=false)
            whenever(local.count()).thenReturn(5)

            // Remote catalog memiliki 3 kategori (hound-afghan, hound-basset, pug)
            whenever(remote.getAllBreedsSync()).thenReturn(
                mapOf(
                    "hound" to listOf("afghan", "basset"),
                    "pug" to null
                )
            )

            // existingIds hanya punya 1 id -> 2 sisanya dianggap baru
            val existingId = BreedCategoryEntity.createId("hound", "afghan")
            whenever(local.allIds()).thenReturn(listOf(existingId))

            // Untuk bagian refreshOldestPreviews(batchSize): tidak penting di test ini, kosongkan
            whenever(local.oldestEntries(any())).thenReturn(emptyList())

            // WHEN
            repo.refreshOldestPreviews(batchSize = 0) // akan call ensureSeeded() + syncRemoteCatalog(false) + oldestEntries(0)

            // THEN
            val captor = argumentCaptor<List<BreedCategoryEntity>>()
            verify(local, atLeastOnce()).upsertAll(captor.capture())

            // Ambil semua batch upsert yang terjadi (bisa 1x atau lebih karena ensureSeeded() + refresh memanggil syncRemoteCatalog(false))
            val combined = captor.allValues.flatten()
            assertTrue("Harus ada insert entri baru", combined.isNotEmpty())

            // Yang bukan existingId harus memiliki lastRefreshTimestamp == 0 (sesuai logika 'force = false')
            val newIds = setOf(
                BreedCategoryEntity.createId("hound", "basset"),
                BreedCategoryEntity.createId("pug", null)
            )
            val newOnes = combined.filter { it.id in newIds }
            assertTrue("Entitas baru harus terinsert", newOnes.isNotEmpty())
            assertTrue(newOnes.all { it.lastRefreshTimestamp == 0L })

            // Pastikan displayName terisi konsisten (sekedar sanity check)
            assertTrue(newOnes.any { it.displayName == "Basset Hound" })
            assertTrue(newOnes.any { it.displayName == "Pug" })
        }

    @Test
    fun `refreshOldestPreviews - updates oldest entries preview using first image from remote`() =
        runTest {
            // GIVEN seeding: local.count() > 0 supaya ensureSeeded() tidak force
            whenever(local.count()).thenReturn(1)
            whenever(remote.getAllBreedsSync()).thenReturn(
                mapOf("beagle" to null) // isi apa pun, tidak krusial untuk test update preview
            )
            whenever(local.allIds()).thenReturn(emptyList()) // tidak ada new category yang perlu diinsert

            // oldest entries to refresh
            val e1 = BreedCategoryEntity(
                id = BreedCategoryEntity.createId("beagle", null),
                breed = "beagle",
                subBreed = null,
                displayName = "Beagle",
                previewImageUrl = null,
                lastRefreshTimestamp = 100L
            )
            val e2 = BreedCategoryEntity(
                id = BreedCategoryEntity.createId("hound", "afghan"),
                breed = "hound",
                subBreed = "afghan",
                displayName = "Afghan Hound",
                previewImageUrl = "oldUrl",
                lastRefreshTimestamp = 50L
            )
            whenever(local.oldestEntries(eq(2))).thenReturn(listOf(e1, e2))

            // remote returns first image per category
            whenever(
                remote.getRandomImagesSync(
                    eq("beagle"),
                    isNull(),
                    eq(1)
                )
            ).thenReturn(listOf("img1"))
            whenever(
                remote.getRandomImagesSync(
                    eq("hound"),
                    eq("afghan"),
                    eq(1)
                )
            ).thenReturn(listOf("img2"))

            // WHEN
            repo.refreshOldestPreviews(batchSize = 2)

            // THEN: updatePreview dipanggil untuk keduanya, dengan URL baru
            verify(local).updatePreview(eq(e1.id), eq("img1"), any())
            verify(local).updatePreview(eq(e2.id), eq("img2"), any())
        }

    @Test
    fun `refreshOldestPreviews - remote catalog empty results in no upsert`() = runTest {
        whenever(local.count()).thenReturn(0) // ensureSeeded() → force = true, tapi remote kosong
        whenever(remote.getAllBreedsSync()).thenReturn(emptyMap())
        whenever(local.oldestEntries(any())).thenReturn(emptyList())

        repo.refreshOldestPreviews(batchSize = 0)

        verify(local, never()).upsertAll(any())
    }
}