package com.example.tasks.series.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasks.series.data.SeriesRepository
import com.example.tasks.series.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class CatalogUiState(
    val query: String = "", val shows: List<Show> = emptyList(),
    val loading: Boolean = true, val error: String? = null, val canLoadMore: Boolean = false
)
data class FavoritesUiState(val shows: List<Show> = emptyList(), val loading: Boolean = true, val error: String? = null)
data class ShowUiState(val show: Show? = null, val episodes: List<Episode> = emptyList(), val loading: Boolean = true, val error: String? = null)

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModel(private val repository: SeriesRepository, private val savedState: SavedStateHandle) : ViewModel() {
    private val _state = MutableStateFlow(CatalogUiState(query = savedState["query"] ?: ""))
    val state = _state.asStateFlow()
    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            savedState.getStateFlow("query", "").collectLatest { query ->
                coroutineScope {
                    _state.update { it.copy(query = query, shows = emptyList(), loading = true, error = null, canLoadMore = false) }
                    launch { repository.observeCollection(query).collect { shows -> _state.update { it.copy(shows = shows) } } }
                    refreshJob?.cancel()
                    refreshJob = launch {
                        if (query.isNotBlank()) delay(500)
                        update(query, false)
                    }
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        if (_state.value.query == query) return
        refreshJob?.cancel()
        // TextField must receive every edit synchronously; collecting SavedStateHandle alone
        // can lag behind rapid keystrokes while the previous request is being cancelled.
        _state.update { it.copy(query = query, loading = true, error = null) }
        savedState["query"] = query
    }
    fun onRefresh(loadMore: Boolean = false) {
        if (state.value.loading) return
        refreshJob?.cancel()
        _state.update { it.copy(loading = true, error = null) }
        refreshJob = viewModelScope.launch { update(state.value.query, loadMore) }
    }
    private suspend fun update(query: String, loadMore: Boolean) {
        val result = repository.refresh(query, loadMore)
        val more = query.isBlank() && repository.canLoadMore()
        _state.update { it.copy(loading = false, error = (result as? RefreshResult.Error)?.message, canLoadMore = more) }
    }
}

class FavoritesViewModel(private val repository: SeriesRepository) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesUiState())
    val state = _state.asStateFlow()
    init {
        viewModelScope.launch { repository.observeFavorites().collect { shows -> _state.update { it.copy(shows = shows, loading = false) } } }
    }
}

class ShowViewModel(private val repository: SeriesRepository, private val showId: Int) : ViewModel() {
    private val _state = MutableStateFlow(ShowUiState())
    val state = _state.asStateFlow()
    private var refreshJob: Job? = null
    init {
        viewModelScope.launch { repository.observeShow(showId).collect { show -> _state.update { it.copy(show = show) } } }
        viewModelScope.launch { repository.observeEpisodes(showId).collect { episodes -> _state.update { it.copy(episodes = episodes) } } }
        refresh()
    }
    fun refresh() {
        if (refreshJob?.isActive == true) return
        if (!state.value.loading) _state.update { it.copy(loading = true, error = null) }
        refreshJob = viewModelScope.launch {
            val result = repository.refreshShow(showId)
            _state.update { it.copy(loading = false, error = (result as? RefreshResult.Error)?.message) }
        }
    }
    fun onToggleFavorite() {
        viewModelScope.launch {
            try { repository.toggleFavorite(showId) }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { _state.update { it.copy(error = "Не удалось сохранить избранное. Повторите.") } }
        }
    }
}
