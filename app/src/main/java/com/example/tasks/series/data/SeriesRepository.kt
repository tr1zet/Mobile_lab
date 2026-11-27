package com.example.tasks.series.data

import android.util.Log
import androidx.room.withTransaction
import com.example.tasks.series.data.local.*
import com.example.tasks.series.data.remote.*
import com.example.tasks.series.model.*
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException

interface SeriesRepository {
    fun observeCollection(query: String = ""): Flow<List<Show>>
    fun observeFavorites(): Flow<List<Show>>
    fun observeShow(id: Int): Flow<Show?>
    fun observeEpisodes(id: Int): Flow<List<Episode>>
    suspend fun refresh(query: String = "", loadMore: Boolean = false): RefreshResult
    suspend fun refreshShow(id: Int): RefreshResult
    suspend fun toggleFavorite(id: Int)
    suspend fun canLoadMore(): Boolean
}

class OfflineFirstSeriesRepository(
    private val database: SeriesDatabase,
    private val api: TvmazeApi
) : SeriesRepository {
    private val dao = database.seriesDao()
    private val refreshMutex = Mutex()

    private fun key(query: String) = if (query.isBlank()) "catalog" else "search:${query.trim().lowercase(java.util.Locale.ROOT)}"
    override fun observeCollection(query: String) = dao.observeCollection(key(query))
    override fun observeFavorites() = dao.observeFavorites()
    override fun observeShow(id: Int) = dao.observeShow(id)
    override fun observeEpisodes(id: Int) = dao.observeEpisodes(id)
    override suspend fun toggleFavorite(id: Int) = dao.toggleFavorite(id)
    override suspend fun canLoadMore() = dao.collection("catalog")?.endReached == false

    // UI always observes Room. Network responses update Room atomically and never erase favorites.
    override suspend fun refresh(query: String, loadMore: Boolean): RefreshResult = refreshMutex.withLock {
        safely {
            val collectionKey = key(query)
            val previous = dao.collection(collectionKey)
            if (loadMore && previous?.endReached == true) return@safely
            val page = if (loadMore) previous?.nextPage ?: 0 else 0
            var endReached = false
            val shows = if (query.isBlank()) {
                try { api.shows(page) } catch (e: HttpException) {
                    if (e.code() == 404 && page > 0) { endReached = true; emptyList() } else throw e
                }
            } else api.search(query.trim()).map { it.show }
            if (shows.isEmpty()) endReached = true
            database.withTransaction {
                if (!loadMore) dao.clearEntries(collectionKey)
                dao.saveShows(shows.map(ShowDto::toEntity))
                dao.saveEntries(shows.mapIndexed { index, show -> CollectionEntry(collectionKey, show.id, page * 250 + index) })
                dao.saveCollection(CollectionEntity(collectionKey, System.currentTimeMillis(), page + 1, endReached))
            }
        }
    }

    override suspend fun refreshShow(id: Int): RefreshResult = safely {
        val show = api.show(id)
        // Save the card even when the separate episodes request fails.
        dao.saveShows(listOf(show.toEntity()))
        val episodes = api.episodes(id)
        database.withTransaction {
            dao.clearEpisodes(id)
            dao.saveEpisodes(episodes.map { EpisodeEntity(it.id, id, it.name ?: "Без названия", it.season, it.number, it.airdate) })
        }
    }

    private suspend fun safely(block: suspend () -> Unit): RefreshResult = try {
        block()
        RefreshResult.Success
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        Log.w("SeriesRepository", "TVmaze network request failed", e)
        RefreshResult.Error("Нет соединения с TVmaze. Сохранённые данные доступны. Проверьте интернет и повторите.")
    } catch (e: HttpException) {
        RefreshResult.Error(when (e.code()) {
            429 -> "Слишком много запросов. Подождите немного и повторите."
            404 -> "Сериал не найден в TVmaze."
            else -> "TVmaze временно недоступен (${e.code()}). Повторите позже."
        })
    } catch (e: Exception) {
        Log.e("SeriesRepository", "Failed to update local catalog", e)
        RefreshResult.Error("Не удалось обновить данные. Попробуйте ещё раз.")
    }
}

internal fun ShowDto.toEntity() = ShowEntity(
    id, name ?: "Без названия", summary.orEmpty(), image?.medium ?: image?.original,
    genres.orEmpty().joinToString(" · "), rating?.average, premiered,
    status.orEmpty(), language.orEmpty(), url ?: "https://www.tvmaze.com/shows/$id"
)
