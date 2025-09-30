package com.raylabs.doggie.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raylabs.doggie.data.DoggieRepository
import com.raylabs.doggie.vo.BreedCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Locale

class SearchViewModel(private val repository: DoggieRepository) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    private val _state = MutableLiveData(SearchUiState(isLoading = true))
    val state: LiveData<SearchUiState> = _state

    init {
        observeCatalog()
    }

    fun onQueryChanged(query: String) {
        if (queryFlow.value == query) return
        Log.d(TAG, "Query changed: $query")
        queryFlow.value = query
    }

    fun onPreviewRequested(category: BreedCategory) {
        repository.requestCategoryPreview(category)
    }

    private fun observeCatalog() {
        viewModelScope.launch {
            repository.observeBreedCatalog()
                .onEach { catalog ->
                    Log.d(TAG, "Catalog size update: ${catalog.size}")
                }
                .onStart {
                    _state.postValue(
                        SearchUiState(
                            isLoading = true,
                            query = queryFlow.value
                        )
                    )
                }
                .catch { throwable ->
                    Log.e(TAG, "Catalog flow error", throwable)
                    _state.postValue(
                        SearchUiState(
                            isLoading = false,
                            query = queryFlow.value,
                            showEmptyState = false,
                            errorMessage = throwable.localizedMessage
                        )
                    )
                }
                .combine(queryFlow) { catalog, query ->
                    val filtered = filter(catalog, query)
                    Log.d(TAG, "Filter applied. Query='$query' results=${filtered.size}")
                    SearchUiState(
                        isLoading = false,
                        results = filtered,
                        query = query,
                        showEmptyState = filtered.isEmpty() && query.isNotBlank(),
                        errorMessage = null
                    )
                }
                .collect { uiState ->
                    Log.d(
                        TAG,
                        "State emission: query='${uiState.query}' count=${uiState.results.size} loading=${uiState.isLoading}"
                    )
                    _state.postValue(uiState)
                }
        }
    }

    private fun filter(data: List<BreedCategory>, query: String): List<BreedCategory> {
        if (query.isBlank()) return data
        val normalizedQuery = normalize(query)
        if (normalizedQuery.isEmpty()) return data
        val hyphenQuery = normalizedQuery.replace(' ', '-')

        return data.filter { category ->
            val displayName = normalize(category.displayName)
            val breed = normalize(category.breed)
            val subBreed = category.subBreed?.let { normalize(it) }.orEmpty()
            val slug = buildString {
                append(category.breed.lowercase(Locale.ROOT))
                category.subBreed?.let { sub ->
                    if (sub.isNotBlank()) {
                        append('-').append(sub.lowercase(Locale.ROOT))
                    }
                }
            }

            displayName.contains(normalizedQuery) ||
                    breed.contains(normalizedQuery) ||
                    subBreed.contains(normalizedQuery) ||
                    slug.contains(hyphenQuery)
        }
    }

    private fun normalize(value: String): String {
        return value
            .lowercase(Locale.ROOT)
            .replace('-', ' ')
            .replace('_', ' ')
            .split(WHITESPACE_REGEX)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    companion object {
        private val WHITESPACE_REGEX = "\\s+".toRegex()
        private const val TAG = "SearchViewModel"
    }
}
