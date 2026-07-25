package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getUserPreferences(): Flow<UserPreferenceEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getUserPreferencesSync(): UserPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(pref: UserPreferenceEntity)

    @Query("UPDATE user_preferences SET dedicationName = :name WHERE id = 1")
    suspend fun updateDedicationName(name: String)

    @Query("UPDATE user_preferences SET calculationMethod = :method WHERE id = 1")
    suspend fun updateCalculationMethod(method: Int)

    @Query("UPDATE user_preferences SET lastSurahNumber = :surahNumber, lastSurahName = :surahName, lastAyahNumber = :ayahNumber WHERE id = 1")
    suspend fun updateLastRead(surahNumber: Int, surahName: String, ayahNumber: Int)

    @Query("UPDATE user_preferences SET totalTasbeehCount = totalTasbeehCount + :increment WHERE id = 1")
    suspend fun incrementTasbeeh(increment: Int)

    @Query("UPDATE user_preferences SET currentCity = :city, latitude = :lat, longitude = :lng WHERE id = 1")
    suspend fun updateLocation(city: String, lat: Double, lng: Double)
}

@Dao
interface DhikrProgressDao {
    @Query("SELECT * FROM dhikr_progress")
    fun getAllDhikrProgress(): Flow<List<DhikrProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: DhikrProgressEntity)

    @Query("DELETE FROM dhikr_progress WHERE category = :category")
    suspend fun resetCategoryProgress(category: String)

    @Query("DELETE FROM dhikr_progress")
    suspend fun resetAllProgress()
}

@Dao
interface QuranBookmarkDao {
    @Query("SELECT * FROM quran_bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<QuranBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: QuranBookmarkEntity)

    @Delete
    suspend fun removeBookmark(bookmark: QuranBookmarkEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM quran_bookmarks WHERE id = :id)")
    suspend fun isBookmarked(id: String): Boolean
}
