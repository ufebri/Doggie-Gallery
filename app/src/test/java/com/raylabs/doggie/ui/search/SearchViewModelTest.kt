package com.raylabs.doggie.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raylabs.doggie.data.DoggieRepository
import com.raylabs.doggie.util.MainDispatcherRule
import com.raylabs.doggie.vo.BreedCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: DoggieRepository = mock()
    private val catalogFlow = MutableStateFlow<List<BreedCategory>>(emptyList())

    private fun createViewModel(): SearchViewModel {
        whenever(repository.observeBreedCatalog()).thenReturn(catalogFlow)
        return SearchViewModel(repository)
    }

    @Test
    fun `initial load emits entire catalog when query empty`() = runTest {
        val afghan = BreedCategory("hound", "afghan", "Afghan Hound", "")
        val pug = BreedCategory("pug", null, "Pug", "")
        val viewModel = createViewModel()

        catalogFlow.value = listOf(afghan, pug)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertFalse(state.isLoading)
        assertEquals(listOf(afghan, pug), state.results)
        assertFalse(state.showEmptyState)
    }

    @Test
    fun `query filters catalog by breed or sub breed`() = runTest {
        val afghan = BreedCategory("hound", "afghan", "Afghan Hound", "")
        val beagle = BreedCategory("hound", null, "Beagle", "")
        val viewModel = createViewModel()

        catalogFlow.value = listOf(afghan, beagle)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.onQueryChanged("afghan")
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertEquals(listOf(afghan), state.results)
        assertFalse(state.isLoading)
        assertFalse(state.showEmptyState)
    }

    @Test
    fun `query with no matches toggles empty state`() = runTest {
        val pug = BreedCategory("pug", null, "Pug", "")
        val viewModel = createViewModel()
        catalogFlow.value = listOf(pug)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.onQueryChanged("husky")
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.getOrAwaitValue()
        assertTrue(state.results.isEmpty())
        assertTrue(state.showEmptyState)
    }

    @Test
    fun `onPreviewRequested delegates to repository`() = runTest {
        val category = BreedCategory("pug", null, "Pug", "")
        val viewModel = createViewModel()

        viewModel.onPreviewRequested(category)

        verify(repository).requestCategoryPreview(category)
    }

    private fun <T> androidx.lifecycle.LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : androidx.lifecycle.Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        observeForever(observer)

        if (!latch.await(time, timeUnit)) {
            removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }

        removeObserver(observer)
        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}
