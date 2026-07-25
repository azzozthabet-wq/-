package com.example.data

import com.example.data.model.AyahInfo
import com.example.data.model.SurahDetail
import com.example.data.model.SurahInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object QuranRepository {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    val surahList = listOf(
        SurahInfo(1, "الفاتحة", "Al-Fatiha", 7, "مكية", 1),
        SurahInfo(2, "البقرة", "Al-Baqarah", 286, "مدنية", 2),
        SurahInfo(3, "آل عمران", "Ali 'Imran", 200, "مدنية", 50),
        SurahInfo(4, "النساء", "An-Nisa", 176, "مدنية", 77),
        SurahInfo(5, "المائدة", "Al-Ma'idah", 120, "مدنية", 106),
        SurahInfo(6, "الأنعام", "Al-An'am", 165, "مكية", 128),
        SurahInfo(7, "الأعراف", "Al-A'raf", 206, "مكية", 151),
        SurahInfo(8, "الأنفال", "Al-Anfal", 75, "مدنية", 177),
        SurahInfo(9, "التوبة", "At-Tawbah", 129, "مدنية", 187),
        SurahInfo(10, "يونس", "Yunus", 109, "مكية", 208),
        SurahInfo(11, "هود", "Hud", 123, "مكية", 221),
        SurahInfo(12, "يوسف", "Yusuf", 111, "مكية", 235),
        SurahInfo(13, "الرعد", "Ar-Ra'd", 43, "مدنية", 249),
        SurahInfo(14, "إبراهيم", "Ibrahim", 52, "مكية", 255),
        SurahInfo(15, "الحجر", "Al-Hijr", 99, "مكية", 262),
        SurahInfo(16, "النحل", "An-Nahl", 128, "مكية", 267),
        SurahInfo(17, "الإسراء", "Al-Isra", 111, "مكية", 282),
        SurahInfo(18, "الكهف", "Al-Kahf", 110, "مكية", 293),
        SurahInfo(19, "مريم", "Maryam", 98, "مكية", 305),
        SurahInfo(20, "طه", "Taha", 135, "مكية", 312),
        SurahInfo(21, "الأنبياء", "Al-Anbiya", 112, "مكية", 322),
        SurahInfo(22, "الحج", "Al-Hajj", 78, "مدنية", 332),
        SurahInfo(23, "المؤمنون", "Al-Mu'minun", 118, "مكية", 342),
        SurahInfo(24, "النور", "An-Nur", 64, "مدنية", 350),
        SurahInfo(25, "الفرقان", "Al-Furqan", 77, "مكية", 359),
        SurahInfo(26, "الشعراء", "Ash-Shu'ara", 227, "مكية", 367),
        SurahInfo(27, "النمل", "An-Naml", 93, "مكية", 377),
        SurahInfo(28, "القصص", "Al-Qasas", 88, "مكية", 385),
        SurahInfo(29, "العنكبوت", "Al-'Ankabut", 69, "مكية", 396),
        SurahInfo(30, "الروم", "Ar-Rum", 60, "مكية", 404),
        SurahInfo(31, "لقمان", "Luqman", 34, "مكية", 411),
        SurahInfo(32, "السجدة", "As-Sajdah", 30, "مكية", 415),
        SurahInfo(33, "الأحزاب", "Al-Ahzab", 73, "مدنية", 418),
        SurahInfo(34, "سبأ", "Saba", 54, "مكية", 428),
        SurahInfo(35, "فاطر", "Fatir", 45, "مكية", 434),
        SurahInfo(36, "يس", "Ya-Sin", 83, "مكية", 440),
        SurahInfo(37, "الصافات", "As-Saffat", 182, "مكية", 446),
        SurahInfo(38, "ص", "Sad", 88, "مكية", 453),
        SurahInfo(39, "الزمر", "Az-Zumar", 75, "مكية", 458),
        SurahInfo(40, "غافر", "Ghafir", 85, "مكية", 467),
        SurahInfo(41, "فصلت", "Fussilat", 54, "مكية", 477),
        SurahInfo(42, "الشورى", "Ash-Shura", 53, "مكية", 483),
        SurahInfo(43, "الزخرف", "Az-Zukhruf", 89, "مكية", 489),
        SurahInfo(44, "الدخان", "Ad-Dukhan", 59, "مكية", 496),
        SurahInfo(45, "الجاثية", "Al-Jathiyah", 37, "مكية", 499),
        SurahInfo(46, "الأحقاف", "Al-Ahqaf", 35, "مكية", 502),
        SurahInfo(47, "محمد", "Muhammad", 38, "مدنية", 507),
        SurahInfo(48, "الفتح", "Al-Fath", 29, "مدنية", 511),
        SurahInfo(49, "الحجرات", "Al-Hujurat", 18, "مدنية", 515),
        SurahInfo(50, "ق", "Qaf", 45, "مكية", 518),
        SurahInfo(51, "الذاريات", "Adh-Dhariyat", 60, "مكية", 520),
        SurahInfo(52, "الطور", "At-Tur", 49, "مكية", 523),
        SurahInfo(53, "النجم", "An-Najm", 62, "مكية", 526),
        SurahInfo(54, "القمر", "Al-Qamar", 55, "مكية", 528),
        SurahInfo(55, "الرحمن", "Ar-Rahman", 78, "مدنية", 531),
        SurahInfo(56, "الواقعة", "Al-Waqi'ah", 96, "مكية", 534),
        SurahInfo(57, "الحديد", "Al-Hadid", 29, "مدنية", 537),
        SurahInfo(58, "المجادلة", "Al-Mujadila", 22, "مدنية", 542),
        SurahInfo(59, "الحشر", "Al-Hashr", 24, "مدنية", 545),
        SurahInfo(60, "الممتحنة", "Al-Mumtahanah", 13, "مدنية", 549),
        SurahInfo(61, "الصف", "As-Saff", 14, "مدنية", 551),
        SurahInfo(62, "الجمعة", "Al-Jumu'ah", 11, "مدنية", 553),
        SurahInfo(63, "المنافقون", "Al-Munafiqun", 11, "مدنية", 554),
        SurahInfo(64, "التغابن", "At-Taghabun", 18, "مدنية", 556),
        SurahInfo(65, "الطلاق", "At-Talaq", 12, "مدنية", 558),
        SurahInfo(66, "التحريم", "At-Tahrim", 12, "مدنية", 560),
        SurahInfo(67, "الملك", "Al-Mulk", 30, "مكية", 562),
        SurahInfo(68, "القلم", "Al-Qalam", 52, "مكية", 564),
        SurahInfo(69, "الحاقة", "Al-Haqqah", 52, "مكية", 566),
        SurahInfo(70, "المعارج", "Al-Ma'arij", 44, "مكية", 568),
        SurahInfo(71, "نوح", "Nuh", 28, "مكية", 570),
        SurahInfo(72, "الجن", "Al-Jinn", 28, "مكية", 572),
        SurahInfo(73, "المزمل", "Al-Muzzammil", 20, "مكية", 574),
        SurahInfo(74, "المدثر", "Al-Muddaththir", 56, "مكية", 575),
        SurahInfo(75, "القيامة", "Al-Qiyamah", 40, "مكية", 577),
        SurahInfo(76, "الإنسان", "Al-Insan", 31, "مدنية", 578),
        SurahInfo(77, "المرسلات", "Al-Mursalat", 50, "مكية", 580),
        SurahInfo(78, "النبأ", "An-Naba", 40, "مكية", 582),
        SurahInfo(79, "النازعات", "An-Nazi'at", 46, "مكية", 583),
        SurahInfo(80, "عبس", "'Abasa", 42, "مكية", 585),
        SurahInfo(81, "التكوير", "At-Takwir", 29, "مكية", 586),
        SurahInfo(82, "الانفطار", "Al-Infitar", 19, "مكية", 587),
        SurahInfo(83, "المطففين", "Al-Mutaffifin", 36, "مكية", 587),
        SurahInfo(84, "الانشقاق", "Al-Inshiqaq", 25, "مكية", 589),
        SurahInfo(85, "البروج", "Al-Buruj", 22, "مكية", 590),
        SurahInfo(86, "الطارق", "At-Tariq", 17, "مكية", 591),
        SurahInfo(87, "الأعلى", "Al-A'la", 19, "مكية", 591),
        SurahInfo(88, "الغاشية", "Al-Ghashiyah", 26, "مكية", 592),
        SurahInfo(89, "الفجر", "Al-Fajr", 30, "مكية", 593),
        SurahInfo(90, "البلد", "Al-Balad", 20, "مكية", 594),
        SurahInfo(91, "الشمس", "Ash-Shams", 15, "مكية", 595),
        SurahInfo(92, "الليل", "Al-Layl", 21, "مكية", 595),
        SurahInfo(93, "الضحى", "Ad-Duha", 11, "مكية", 596),
        SurahInfo(94, "الشرح", "Ash-Sharh", 8, "مكية", 596),
        SurahInfo(95, "التين", "At-Tin", 8, "مكية", 597),
        SurahInfo(96, "العلق", "Al-'Alaq", 19, "مكية", 597),
        SurahInfo(97, "القدر", "Al-Qadr", 5, "مكية", 598),
        SurahInfo(98, "البينة", "Al-Bayyinah", 8, "مدنية", 598),
        SurahInfo(99, "الزلزلة", "Az-Zalzalah", 8, "مدنية", 599),
        SurahInfo(100, "العاديات", "Al-'Adiyat", 11, "مكية", 599),
        SurahInfo(101, "القارعة", "Al-Qari'ah", 11, "مكية", 600),
        SurahInfo(102, "التكاثر", "At-Takathur", 8, "مكية", 600),
        SurahInfo(103, "العصر", "Al-'Asr", 3, "مكية", 601),
        SurahInfo(104, "الهمزة", "Al-Humazah", 9, "مكية", 601),
        SurahInfo(105, "الفيل", "Al-Fil", 5, "مكية", 601),
        SurahInfo(106, "قريش", "Quraysh", 4, "مكية", 602),
        SurahInfo(107, "الماعون", "Al-Ma'un", 7, "مكية", 602),
        SurahInfo(108, "الكوثر", "Al-Kawthar", 3, "مكية", 602),
        SurahInfo(109, "الكافرون", "Al-Kafirun", 6, "مكية", 603),
        SurahInfo(110, "النصر", "An-Nasr", 3, "مدنية", 603),
        SurahInfo(111, "المسد", "Al-Masad", 5, "مكية", 603),
        SurahInfo(112, "الإخلاص", "Al-Ikhlas", 4, "مكية", 604),
        SurahInfo(113, "الفلق", "Al-Falaq", 5, "مكية", 604),
        SurahInfo(114, "الناس", "An-Nas", 6, "مكية", 604)
    )

    suspend fun getSurahDetail(surahNumber: Int): SurahDetail = withContext(Dispatchers.IO) {
        // First try to fetch online from alquran.cloud API
        try {
            val url = "https://api.alquran.cloud/v1/surah/$surahNumber"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            if (response.isSuccessful && !body.isNullOrEmpty()) {
                val json = JSONObject(body)
                val data = json.getJSONObject("data")
                val nameAr = data.getString("name")
                val nameEn = data.getString("englishName")
                val numAyahs = data.getInt("numberOfAyahs")
                val revType = if (data.getString("revelationType").equals("Meccan", ignoreCase = true)) "مكية" else "مدنية"

                val ayahsJson = data.getJSONArray("ayahs")
                val ayahsList = mutableListOf<AyahInfo>()
                for (i in 0 until ayahsJson.length()) {
                    val aObj = ayahsJson.getJSONObject(i)
                    ayahsList.add(
                        AyahInfo(
                            numberInSurah = aObj.getInt("numberInSurah"),
                            globalNumber = aObj.getInt("number"),
                            text = aObj.getString("text"),
                            juz = aObj.optInt("juz", 1),
                            page = aObj.optInt("page", 1)
                        )
                    )
                }

                return@withContext SurahDetail(
                    number = surahNumber,
                    nameAr = nameAr,
                    nameEn = nameEn,
                    numberOfAyahs = numAyahs,
                    revelationType = revType,
                    ayahs = ayahsList
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Offline fallback for popular Surahs if offline
        return@withContext getOfflineSurahDetail(surahNumber)
    }

    private fun getOfflineSurahDetail(surahNumber: Int): SurahDetail {
        val meta = surahList.find { it.number == surahNumber } ?: surahList[0]

        val sampleAyahs = when (surahNumber) {
            1 -> listOf(
                "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                "الرَّحْمَٰنِ الرَّحِيمِ",
                "مَالِكِ يَوْمِ الدِّينِ",
                "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
                "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ"
            )
            112 -> listOf(
                "قُلْ هُوَ اللَّهُ أَحَدٌ",
                "اللَّهُ الصَّمَدُ",
                "لَمْ يَلِدْ وَلَمْ يُولَدْ",
                "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ"
            )
            113 -> listOf(
                "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ",
                "مِن شَرِّ مَا خَلَقَ",
                "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ",
                "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ",
                "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ"
            )
            114 -> listOf(
                "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                "مَلِكِ النَّاسِ",
                "إِلَٰهِ النَّاسِ",
                "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ",
                "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ",
                "مِنَ الْجِنَّةِ وَالنَّاسِ"
            )
            else -> List(meta.numberOfAyahs) { idx ->
                "آية رقم ${idx + 1} من سورة ${meta.nameAr} - قراءة القران الكريم نور وهداية للقلوب"
            }
        }

        val ayahsList = sampleAyahs.mapIndexed { index, text ->
            AyahInfo(
                numberInSurah = index + 1,
                globalNumber = index + 1,
                text = text,
                juz = 1,
                page = meta.page
            )
        }

        return SurahDetail(
            number = meta.number,
            nameAr = meta.nameAr,
            nameEn = meta.nameEn,
            numberOfAyahs = meta.numberOfAyahs,
            revelationType = meta.revelationType,
            ayahs = ayahsList
        )
    }

    fun getAudioUrlForSurah(surahNumber: Int): String {
        val formattedNumber = String.format("%03d", surahNumber)
        return "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/$formattedNumber.mp3"
    }
}
