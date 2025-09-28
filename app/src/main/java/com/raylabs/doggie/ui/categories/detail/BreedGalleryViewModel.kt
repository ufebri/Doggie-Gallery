package com.raylabs.doggie.ui.categories.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.raylabs.doggie.data.DoggieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

class BreedGalleryViewModel(private val repository: DoggieRepository) : ViewModel() {

    private val paramsFlow = MutableStateFlow<BreedParams?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val images: Flow<PagingData<String>> =
        paramsFlow.filterNotNull()
            .flatMapLatest { params ->
                repository.getBreedImages(params.breed, params.subBreed, PAGE_SIZE)
            }
            .cachedIn(viewModelScope)

    fun setBreed(breed: String, subBreed: String?) {
        val current = paramsFlow.value
        if (current?.breed == breed && current.subBreed == subBreed) {
            return
        }
        paramsFlow.value = BreedParams(breed, subBreed)
    }

    private data class BreedParams(val breed: String, val subBreed: String?)

    companion object {
        const val PAGE_SIZE = 10
    }
}
