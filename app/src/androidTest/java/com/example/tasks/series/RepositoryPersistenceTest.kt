package com.example.tasks.series

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.tasks.series.data.OfflineFirstSeriesRepository
import com.example.tasks.series.data.local.SeriesDatabase
import com.example.tasks.series.data.remote.*
import com.example.tasks.series.model.RefreshResult
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryPersistenceTest {
    @Test fun cachedDataAndFavoritesSurviveDatabaseReopenAndNetworkFailure() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "persistence-test.db"
        context.deleteDatabase(name)
        val api = TestApi()
        var db = Room.databaseBuilder(context, SeriesDatabase::class.java, name).build()
        try {
            var repo = OfflineFirstSeriesRepository(db, api)
            assertEquals(RefreshResult.Success, repo.refresh())
            assertEquals(RefreshResult.Success, repo.refreshShow(1))
            repo.toggleFavorite(1)
            // A refresh updates metadata without overwriting the user's favorite.
            repo.refresh()
            assertTrue(repo.observeShow(1).first()!!.favorite)
            db.close()
            db = Room.databaseBuilder(context, SeriesDatabase::class.java, name).build()
            repo = OfflineFirstSeriesRepository(db, api)
            api.offline = true
            assertTrue(repo.refresh() is RefreshResult.Error)
            assertTrue(repo.refreshShow(1) is RefreshResult.Error)
            assertEquals("Test show", repo.observeCollection().first().single().name)
            assertEquals(1, repo.observeFavorites().first().single().id)
            assertEquals("Pilot", repo.observeEpisodes(1).first().single().name)
            assertTrue(repo.observeShow(1).first()!!.favorite)
        } finally {
            db.close()
            context.deleteDatabase(name)
        }
    }

    @Test fun searchesStaySeparateAndPagesAppendWithoutDuplicates() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, SeriesDatabase::class.java).build()
        try {
            val api = TestApi()
            val repo = OfflineFirstSeriesRepository(db, api)
            repo.refresh()
            repo.refresh(loadMore = true)
            assertEquals(listOf(1, 2), repo.observeCollection().first().map { it.id })
            repo.refresh("first")
            repo.refresh("second")
            assertEquals(10, repo.observeCollection("first").first().single().id)
            assertEquals(20, repo.observeCollection("second").first().single().id)
            api.offline = true
            repo.refresh("first")
            assertEquals(10, repo.observeCollection("first").first().single().id)
        } finally { db.close() }
    }
}

private class TestApi : TvmazeApi {
    var offline = false
    private fun card(id: Int) = ShowDto(id, "Test show", null, null, null, null, null, null, null, null)
    private fun checkNetwork() { if (offline) throw IOException("offline") }
    override suspend fun shows(page: Int): List<ShowDto> { checkNetwork(); return listOf(card(page + 1)) }
    override suspend fun search(query: String): List<SearchDto> { checkNetwork(); return listOf(SearchDto(card(if (query == "first") 10 else 20))) }
    override suspend fun show(id: Int): ShowDto { checkNetwork(); return card(id) }
    override suspend fun episodes(id: Int): List<EpisodeDto> { checkNetwork(); return listOf(EpisodeDto(1, "Pilot", 1, 1, null)) }
}
