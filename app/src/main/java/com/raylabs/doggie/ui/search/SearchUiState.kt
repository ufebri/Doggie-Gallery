package com.raylabs.doggie.ui.search

import com.raylabs.doggie.vo.BreedCategory

data class SearchUiState(
    val isLoading: Boolean = false,
    val results: List<BreedCategory> = emptyList(),
    val query: String = "",
    val showEmptyState: Boolean = false,
    val errorMessage: String? = null
)
