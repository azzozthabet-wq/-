package com.example.data.model

data class PrayerTimes(
    val fajr: String,
    val shorooq: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val dateGregorian: String,
    val dateHijri: String,
    val city: String
)

data class NextPrayerInfo(
    val nameAr: String,
    val timeFormatted: String,
    val remainingMillis: Long,
    val progressFraction: Float
)

data class CalculationMethodInfo(
    val id: Int,
    val nameAr: String
)
