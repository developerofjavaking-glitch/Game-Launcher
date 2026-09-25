package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // --- Sessions ---
    @Query("SELECT * FROM game_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_sessions WHERE packageName = :packageName ORDER BY startTime DESC")
    fun getSessionsForGame(packageName: String): Flow<List<GameSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity): Long

    @Query("DELETE FROM game_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("DELETE FROM game_sessions")
    suspend fun clearAllSessions()

    // --- Game Profiles ---
    @Query("SELECT * FROM game_profiles ORDER BY isFavorite DESC, totalPlayTimeMillis DESC, title ASC")
    fun getAllProfiles(): Flow<List<GameProfileEntity>>

    @Query("SELECT * FROM game_profiles WHERE packageName = :packageName LIMIT 1")
    suspend fun getProfile(packageName: String): GameProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: GameProfileEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfilesIfNotExists(profiles: List<GameProfileEntity>)

    @Update
    suspend fun updateProfile(profile: GameProfileEntity)

    @Query("UPDATE game_profiles SET isFavorite = :isFavorite WHERE packageName = :packageName")
    suspend fun setFavorite(packageName: String, isFavorite: Boolean)

    @Query("UPDATE game_profiles SET performanceMode = :mode WHERE packageName = :packageName")
    suspend fun updatePerformanceMode(packageName: String, mode: String)

    @Query("UPDATE game_profiles SET totalPlayTimeMillis = totalPlayTimeMillis + :playTime, launchCount = launchCount + 1, lastPlayedTimestamp = :timestamp WHERE packageName = :packageName")
    suspend fun recordGamePlay(packageName: String, playTime: Long, timestamp: Long)

    @Query("DELETE FROM game_profiles WHERE packageName = :packageName")
    suspend fun deleteProfile(packageName: String)

    // --- Bookmarked News ---
    @Query("SELECT * FROM bookmarked_news ORDER BY timestamp DESC")
    fun getBookmarkedNews(): Flow<List<BookmarkedNewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bookmarkNews(news: BookmarkedNewsEntity)

    @Query("DELETE FROM bookmarked_news WHERE newsId = :newsId")
    suspend fun removeBookmark(newsId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_news WHERE newsId = :newsId)")
    fun isNewsBookmarked(newsId: String): Flow<Boolean>
}
