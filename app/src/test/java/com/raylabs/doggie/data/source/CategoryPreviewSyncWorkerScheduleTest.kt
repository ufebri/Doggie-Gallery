package com.raylabs.doggie.data.source

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.raylabs.doggie.data.BreedCategoryRepositoryHelper
import com.raylabs.doggie.data.CategoryPreviewSyncWorker
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CategoryPreviewSyncWorkerTest {

    @Test
    fun `doWork returns success`() = runTest {
        val ctx = mock<Context>()
        val params = mock<WorkerParameters>()

        val fakeHelper = mock<BreedCategoryRepositoryHelper>()
        whenever(fakeHelper.refreshOldestPreviews(any())).thenAnswer { }

        val worker = CategoryPreviewSyncWorker(ctx, params) { _ -> fakeHelper }

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success()::class, result::class)
        verify(fakeHelper).refreshOldestPreviews(20)
    }

    @Test
    fun `doWork returns retry when helper throws`() = runTest {
        val ctx = mock<Context>()
        val params = mock<WorkerParameters>()

        val fakeHelper = mock<BreedCategoryRepositoryHelper>()
        whenever(fakeHelper.refreshOldestPreviews(any()))
            .thenThrow(RuntimeException("boom"))

        val worker = CategoryPreviewSyncWorker(ctx, params) { _ -> fakeHelper }

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.retry()::class, result::class)
    }
}