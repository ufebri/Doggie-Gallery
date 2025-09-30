package com.raylabs.doggie.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.raylabs.doggie.data.DoggieRepository
import com.raylabs.doggie.vo.BreedCategory
import kotlinx.coroutines.flow.Flow

class CategoriesViewModel(
    private val repository: DoggieRepository
) : ViewModel() {

    val data: Flow<PagingData<BreedCategory>> =
        repository.getCategories().cachedIn(viewModelScope)

    fun requestPreview(category: BreedCategory) {
        repository.requestCategoryPreview(category)
    }
}
