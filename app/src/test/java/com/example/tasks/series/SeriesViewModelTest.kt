package com.example.tasks.series

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.example.tasks.MainDispatcherRule
import com.example.tasks.series.data.SeriesRepository
import com.example.tasks.series.data.toEntity
import com.example.tasks.series.data.remote.ShowDto
import com.example.tasks.series.model.*
import com.example.tasks.series.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeriesViewModelTest {
    @get:Rule val dispatcher = MainDispatcherRule()
    private val show = Show(1, "Under the Dome", "", null, "Drama", null, null, "Ended", "English", "https://www.tvmaze.com/shows/1")

    @Test fun cachedCatalogSurvivesNetworkFailure() = runTest {
        val repo = FakeRepository(listOf(show)).apply { result = RefreshResult.Error("Нет сети") }
        val store = ViewModelStore()
        try {
            val vm = stored(store) { CatalogViewModel(repo, SavedStateHandle()) }
            runCurrent()
            assertEquals(listOf(show), vm.state.value.shows)
            assertEquals("Нет сети", vm.state.value.error)
            assertFalse(vm.state.value.loading)
        } finally { store.clear() }
    }

    @Test fun searchDebouncesAndCancelsPreviousQuery() = runTest {
        val repo = FakeRepository()
        val store = ViewModelStore()
        try {
            val vm = stored(store) { CatalogViewModel(repo, SavedStateHandle()) }
            runCurrent()
            vm.onQueryChanged("Sher")
            runCurrent()
            advanceTimeBy(300)
            vm.onQueryChanged("Sherlock")
            runCurrent()
            advanceTimeBy(501)
            runCurrent()
            assertEquals(listOf("", "Sherlock"), repo.requests)
            assertEquals("Sherlock", vm.state.value.query)
        } finally { store.clear() }
    }

    @Test fun textEditsArePublishedBeforeCollectorResumes() = runTest {
        val store = ViewModelStore()
        try {
            val vm = stored(store) { CatalogViewModel(FakeRepository(), SavedStateHandle()) }
            runCurrent()
            vm.onQueryChanged("S")
            assertEquals("S", vm.state.value.query)
            vm.onQueryChanged("Sherlock")
            assertEquals("Sherlock", vm.state.value.query)
            runCurrent()
            assertEquals("Sherlock", vm.state.value.query)
        } finally { store.clear() }
    }

    @Test fun queryRestoredFromSavedState() = runTest {
        val repo = FakeRepository()
        val store = ViewModelStore()
        try {
            val vm = stored(store) { CatalogViewModel(repo, SavedStateHandle(mapOf("query" to "Dark"))) }
            runCurrent()
            advanceTimeBy(501)
            runCurrent()
            assertEquals("Dark", vm.state.value.query)
            assertEquals(listOf("Dark"), repo.requests)
        } finally { store.clear() }
    }

    @Test fun favoritesReactToRepositoryChanges() = runTest {
        val repo = FakeRepository(listOf(show))
        val store = ViewModelStore()
        try {
            val vm = stored(store) { FavoritesViewModel(repo) }
            runCurrent()
            assertTrue(vm.state.value.shows.isEmpty())
            repo.toggleFavorite(1)
            runCurrent()
            assertTrue(vm.state.value.shows.single().favorite)
            repo.toggleFavorite(1)
            runCurrent()
            assertTrue(vm.state.value.shows.isEmpty())
        } finally { store.clear() }
    }

    @Test fun detailShowsCachedCardAndEpisodesWhenOffline() = runTest {
        val repo = FakeRepository(listOf(show)).apply {
            result = RefreshResult.Error("Нет сети")
            episodes.value = listOf(Episode(1, 1, "Pilot", 1, 1, "2013-06-24"))
        }
        val store = ViewModelStore()
        try {
            val vm = stored(store) { ShowViewModel(repo, 1) }
            runCurrent()
            assertEquals(show, vm.state.value.show)
            assertEquals("Pilot", vm.state.value.episodes.single().name)
            assertFalse(vm.state.value.loading)
            assertNotNull(vm.state.value.error)
        } finally { store.clear() }
    }

    @Test fun incompleteApiDataHasSafeDefaults() {
        val entity = ShowDto(42, null, null, null, null, null, null, null, null, null).toEntity()
        assertEquals("Без названия", entity.name)
        assertEquals("", entity.summary)
        assertNull(entity.rating)
        assertEquals("https://www.tvmaze.com/shows/42", entity.url)
    }

    @Test fun clearingViewModelCancelsNetworkWork() = runTest {
        val repo = FakeRepository().apply { blockRefresh = true }
        val store = ViewModelStore()
        stored(store) { CatalogViewModel(repo, SavedStateHandle()) }
        runCurrent()
        store.clear()
        runCurrent()
        assertTrue(repo.cancelled)
    }

    private inline fun <reified T : ViewModel> stored(store: ViewModelStore, crossinline build: () -> T): T =
        ViewModelProvider(store, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <V : ViewModel> create(modelClass: Class<V>): V = build() as V
        })[T::class.java]
}

private class FakeRepository(shows: List<Show> = emptyList()) : SeriesRepository {
    val data = MutableStateFlow(shows)
    val episodes = MutableStateFlow<List<Episode>>(emptyList())
    val requests = mutableListOf<String>()
    var result: RefreshResult = RefreshResult.Success
    var blockRefresh = false
    var cancelled = false
    override fun observeCollection(query: String) = data
    override fun observeFavorites() = data.map { list -> list.filter { it.favorite } }
    override fun observeShow(id: Int) = data.map { list -> list.find { it.id == id } }
    override fun observeEpisodes(id: Int) = episodes
    override suspend fun refresh(query: String, loadMore: Boolean): RefreshResult {
        requests += query
        if (blockRefresh) try { awaitCancellation() } finally { cancelled = true }
        return result
    }
    override suspend fun refreshShow(id: Int) = result
    override suspend fun toggleFavorite(id: Int) { data.value = data.value.map { if (it.id == id) it.copy(favorite = !it.favorite) else it } }
    override suspend fun canLoadMore() = true
}
