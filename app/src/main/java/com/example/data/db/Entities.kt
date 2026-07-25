package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val dedicationName: String = "جَدِّي المرحوم",
    val calculationMethod: Int = 4, // 4 = Umm al-Qura
    val lastSurahNumber: Int = 1,
    val lastSurahName: String = "الفاتحة",
    val lastAyahNumber: Int = 1,
    val totalTasbeehCount: Int = 0,
    val currentCity: String = "مكة المكرمة",
    val latitude: Double = 21.4225,
    val longitude: Double = 39.8262
)

@Entity(tableName = "dhikr_progress")
data class DhikrProgressEntity(
    @PrimaryKey val dhikrId: String,
    val category: String,
    val currentCount: Int,
    val targetCount: Int,
    val isCompleted: Boolean = false
)

@Entity(tableName = "quran_bookmarks")
data class QuranBookmarkEntity(
    @PrimaryKey val id: String, // e.g. "1_1" for surah 1, ayah 1
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
