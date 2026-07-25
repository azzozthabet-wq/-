package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AdhkarRepository
import com.example.data.PrayerTimeCalculator
import com.example.data.QuranRepository
import com.example.data.db.AppDatabase
import com.example.data.db.DhikrProgressEntity
import com.example.data.db.UserPreferenceEntity
import com.example.data.model.NextPrayerInfo
import com.example.data.model.PrayerTimes
import com.example.data.model.SurahDetail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MishkatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val prefDao = db.userPreferenceDao()
    private val dhikrDao = db.dhikrProgressDao()
    private val bookmarkDao = db.quranBookmarkDao()

    val userPreferences: StateFlow<UserPreferenceEntity> = prefDao.getUserPreferences()
        .map { it ?: UserPreferenceEntity() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferenceEntity())

    val dhikrProgress: StateFlow<List<DhikrProgressEntity>> = dhikrDao.getAllDhikrProgress()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val bookmarks = bookmarkDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    private val _selectedSurahDetail = MutableStateFlow<SurahDetail?>(null)
    val selectedSurahDetail: StateFlow<SurahDetail?> = _selectedSurahDetail.asStateFlow()

    private val _isLoadingSurah = MutableStateFlow(false)
    val isLoadingSurah: StateFlow<Boolean> = _isLoadingSurah.asStateFlow()

    init {
        // Ensure default preference row exists
        viewModelScope.launch {
            if (prefDao.getUserPreferencesSync() == null) {
                prefDao.insertOrUpdatePreferences(UserPreferenceEntity())
            }
        }

        // Live clock tick for countdown
        viewModelScope.launch {
            while (true) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    val prayerTimes: StateFlow<PrayerTimes> = combine(userPreferences, _currentTimeMillis) { pref, _ ->
        val cal = Calendar.getInstance()
        PrayerTimeCalculator.calculatePrayerTimes(
            latitude = pref.latitude,
            longitude = pref.longitude,
            methodId = pref.calculationMethod,
            date = cal,
            cityName = pref.currentCity
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PrayerTimeCalculator.calculatePrayerTimes()
    )

    val nextPrayerInfo: StateFlow<NextPrayerInfo> = combine(prayerTimes, _currentTimeMillis) { pt, now ->
        PrayerTimeCalculator.getNextPrayerInfo(pt, now)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PrayerTimeCalculator.getNextPrayerInfo(PrayerTimeCalculator.calculatePrayerTimes())
    )

    val qiblaBearing: StateFlow<Double> = userPreferences.map { pref ->
        PrayerTimeCalculator.calculateQiblaBearing(pref.latitude, pref.longitude)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 135.0)

    fun updateDedicationName(newName: String) {
        viewModelScope.launch {
            prefDao.updateDedicationName(newName.ifBlank { "جَدِّي المرحوم" })
        }
    }

    fun updateCalculationMethod(methodId: Int) {
        viewModelScope.launch {
            prefDao.updateCalculationMethod(methodId)
        }
    }

    fun updateLastRead(surahNumber: Int, surahName: String, ayahNumber: Int) {
        viewModelScope.launch {
            prefDao.updateLastRead(surahNumber, surahName, ayahNumber)
        }
    }

    fun updateLocation(cityName: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            prefDao.updateLocation(cityName, lat, lng)
        }
    }

    fun incrementDhikr(dhikrId: String, category: String, targetCount: Int) {
        viewModelScope.launch {
            val currentList = dhikrProgress.value
            val existing = currentList.find { it.dhikrId == dhikrId }
            val currentCount = existing?.currentCount ?: 0
            val newCount = currentCount + 1
            val isCompleted = newCount >= targetCount

            dhikrDao.saveProgress(
                DhikrProgressEntity(
                    dhikrId = dhikrId,
                    category = category,
                    currentCount = newCount,
                    targetCount = targetCount,
                    isCompleted = isCompleted
                )
            )
        }
    }

    fun resetCategoryDhikr(category: String) {
        viewModelScope.launch {
            dhikrDao.resetCategoryProgress(category)
        }
    }

    fun incrementTasbeeh() {
        viewModelScope.launch {
            prefDao.incrementTasbeeh(1)
        }
    }

    fun loadSurah(surahNumber: Int) {
        viewModelScope.launch {
            _isLoadingSurah.value = true
            val detail = QuranRepository.getSurahDetail(surahNumber)
            _selectedSurahDetail.value = detail
            _isLoadingSurah.value = false
            updateLastRead(detail.number, detail.nameAr, 1)
        }
    }
}
