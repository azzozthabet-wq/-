package com.example.data.model

data class SurahInfo(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val numberOfAyahs: Int,
    val revelationType: String, // مكية or مدنية
    val page: Int
)

data class AyahInfo(
    val numberInSurah: Int,
    val globalNumber: Int,
    val text: String,
    val juz: Int,
    val page: Int
)

data class SurahDetail(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val numberOfAyahs: Int,
    val revelationType: String,
    val ayahs: List<AyahInfo>
)
