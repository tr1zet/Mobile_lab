package com.example.tasks.series.data.local

import androidx.room.*
import com.example.tasks.series.model.Episode
import com.example.tasks.series.model.Show
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "shows")
data class ShowEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val summary: String,
    val image: String?,
    val genres: String,
    val rating: Double?,
    val premiered: String?,
    val status: String,
    val language: String,
    val url: String
)

@Entity(tableName = "favorites")
data class FavoriteEntity(@PrimaryKey val showId: Int)

@Entity(tableName = "collection_entries", primaryKeys = ["collection", "showId"])
data class CollectionEntry(val collection: String, val showId: Int, val position: Int)

@Entity(tableName = "collections")
data class CollectionEntity(@PrimaryKey val key: String, val updatedAt: Long, val nextPage: Int, val endReached: Boolean)

@Entity(tableName = "episodes", indices = [Index("showId")])
data class EpisodeEntity(
    @PrimaryKey val id: Int, val showId: Int, val name: String,
    val season: Int, val number: Int?, val airdate: String?
)

@Dao
interface SeriesDao {
    @Query("SELECT s.*, EXISTS(SELECT 1 FROM favorites f WHERE f.showId = s.id) AS favorite FROM shows s JOIN collection_entries e ON e.showId = s.id WHERE e.collection = :key ORDER BY e.position")
    fun observeCollection(key: String): Flow<List<Show>>

    @Query("SELECT s.*, 1 AS favorite FROM shows s JOIN favorites f ON f.showId = s.id ORDER BY s.name")
    fun observeFavorites(): Flow<List<Show>>

    @Query("SELECT s.*, EXISTS(SELECT 1 FROM favorites f WHERE f.showId = s.id) AS favorite FROM shows s WHERE s.id = :id")
    fun observeShow(id: Int): Flow<Show?>

    @Query("SELECT * FROM episodes WHERE showId = :id ORDER BY season, number, id")
    fun observeEpisodes(id: Int): Flow<List<Episode>>

    @Query("SELECT * FROM collections WHERE `key` = :key")
    suspend fun collection(key: String): CollectionEntity?

    @Upsert suspend fun saveShows(shows: List<ShowEntity>)
    @Upsert suspend fun saveEntries(entries: List<CollectionEntry>)
    @Upsert suspend fun saveCollection(collection: CollectionEntity)
    @Upsert suspend fun saveEpisodes(episodes: List<EpisodeEntity>)
    @Query("DELETE FROM collection_entries WHERE collection = :key") suspend fun clearEntries(key: String)
    @Query("DELETE FROM episodes WHERE showId = :id") suspend fun clearEpisodes(id: Int)
    @Query("DELETE FROM favorites WHERE showId = :id") suspend fun removeFavorite(id: Int)
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun addFavorite(favorite: FavoriteEntity)
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE showId = :id)") suspend fun isFavorite(id: Int): Boolean

    @Transaction
    suspend fun toggleFavorite(id: Int) {
        if (isFavorite(id)) removeFavorite(id) else addFavorite(FavoriteEntity(id))
    }
}

@Database(
    entities = [ShowEntity::class, FavoriteEntity::class, CollectionEntry::class, CollectionEntity::class, EpisodeEntity::class],
    version = 1, exportSchema = true
)
abstract class SeriesDatabase : RoomDatabase() {
    abstract fun seriesDao(): SeriesDao
}
