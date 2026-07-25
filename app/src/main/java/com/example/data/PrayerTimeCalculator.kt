package com.example.data

import com.example.data.model.CalculationMethodInfo
import com.example.data.model.NextPrayerInfo
import com.example.data.model.PrayerTimes
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

object PrayerTimeCalculator {

    val calculationMethods = listOf(
        CalculationMethodInfo(4, "أم القرى - مكة المكرمة"),
        CalculationMethodInfo(3, "رابطة العالم الإسلامي"),
        CalculationMethodInfo(5, "الهيئة المصرية العامة للمساحة"),
        CalculationMethodInfo(1, "جامعة العلوم الإسلامية بكراتشي"),
        CalculationMethodInfo(2, "الجمعية الإسلامية لشمال أمريكا (ISNA)")
    )

    fun calculatePrayerTimes(
        latitude: Double = 21.4225, // Default Mecca
        longitude: Double = 39.8262,
        methodId: Int = 4,
        date: Calendar = Calendar.getInstance(),
        cityName: String = "مكة المكرمة"
    ): PrayerTimes {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1
        val day = date.get(Calendar.DAY_OF_MONTH)

        // Standard calculation parameters based on method
        val (fajrAngle, ishaAngle, ishaMinutes) = when (methodId) {
            4 -> Triple(18.5, 0.0, 90) // Umm Al-Qura (Isha is +90 min after Maghrib)
            3 -> Triple(18.0, 17.0, 0) // MWL
            5 -> Triple(19.5, 17.5, 0) // Egyptian
            1 -> Triple(18.0, 18.0, 0) // Karachi
            2 -> Triple(15.0, 15.0, 0) // ISNA
            else -> Triple(18.5, 0.0, 90)
        }

        // Astronomical calculations
        val d = julianDay(year, month, day) - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val L = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val RA = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(L)), cos(Math.toRadians(L))))) / 15.0

        val eqt = q / 15.0 - fixHour(RA)
        val decl = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(L))))

        // Time zone offset in hours
        val timeZone = date.timeZone.getOffset(date.timeInMillis) / (1000.0 * 3600.0)

        val dhuhrUtc = 12.0 + timeZone - longitude / 15.0 - eqt

        val fajrUtc = dhuhrUtc - sunAngleTime(fajrAngle, latitude, decl)
        val shorooqUtc = dhuhrUtc - sunAngleTime(0.833, latitude, decl)
        val asrUtc = dhuhrUtc + asrTime(1, latitude, decl)
        val maghribUtc = dhuhrUtc + sunAngleTime(0.833, latitude, decl)
        val ishaUtc = if (ishaMinutes > 0) {
            maghribUtc + (ishaMinutes / 60.0)
        } else {
            dhuhrUtc + sunAngleTime(ishaAngle, latitude, decl)
        }

        val df = SimpleDateFormat("HH:mm", Locale("ar"))
        df.timeZone = date.timeZone

        val fajrStr = formatTime(fajrUtc)
        val shorooqStr = formatTime(shorooqUtc)
        val dhuhrStr = formatTime(dhuhrUtc)
        val asrStr = formatTime(asrUtc)
        val maghribStr = formatTime(maghribUtc)
        val ishaStr = formatTime(ishaUtc)

        val gregFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale("ar"))
        val gregDate = gregFormat.format(date.time)

        val hijriDateStr = getApproximateHijriDate(date)

        return PrayerTimes(
            fajr = fajrStr,
            shorooq = shorooqStr,
            dhuhr = dhuhrStr,
            asr = asrStr,
            maghrib = maghribStr,
            isha = ishaStr,
            dateGregorian = gregDate,
            dateHijri = hijriDateStr,
            city = cityName
        )
    }

    private fun formatTime(hoursFloat: Double): String {
        var h = hoursFloat.toInt()
        val m = ((hoursFloat - h) * 60).toInt()
        val sec = (((hoursFloat - h) * 60 - m) * 60).toInt()
        var adjustedH = (h % 24 + 24) % 24
        return String.format(Locale("ar"), "%02d:%02d", adjustedH, abs(m))
    }

    private fun sunAngleTime(angle: Double, lat: Double, decl: Double): Double {
        val cosH = (-sin(Math.toRadians(angle)) - sin(Math.toRadians(lat)) * sin(Math.toRadians(decl))) /
                (cos(Math.toRadians(lat)) * cos(Math.toRadians(decl)))
        if (cosH > 1.0) return 0.0
        if (cosH < -1.0) return 12.0
        return Math.toDegrees(acos(cosH)) / 15.0
    }

    private fun asrTime(factor: Int, lat: Double, decl: Double): Double {
        val phi = Math.toRadians(lat)
        val delta = Math.toRadians(decl)
        val cotG = factor + tan(abs(phi - delta))
        val cosH = (sin(atan(1.0 / cotG)) - sin(phi) * sin(delta)) / (cos(phi) * cos(delta))
        if (cosH > 1.0) return 0.0
        if (cosH < -1.0) return 12.0
        return Math.toDegrees(acos(cosH)) / 15.0
    }

    private fun julianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(a: Double): Double {
        var b = a - 360.0 * floor(a / 360.0)
        if (b < 0) b += 360.0
        return b
    }

    private fun fixHour(a: Double): Double {
        var b = a - 24.0 * floor(a / 24.0)
        if (b < 0) b += 24.0
        return b
    }

    private fun getApproximateHijriDate(cal: Calendar): String {
        // Approximate Hijri conversion algorithm
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val jd = julianDay(year, month, day).toInt()
        val l = jd - 1948440 + 10632
        val n = (l - 1) / 10631
        val l2 = l - 10631 * n + 354
        val j = ((10985 - l2) / 5316) * ((50 * l2) / 17719) + (l2 / 5670) * ((43 * l2) / 15238)
        val l3 = l2 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val m = (24 * l3) / 709
        val d = l3 - (709 * m) / 24
        val y = 30 * n + j - 30

        val hijriMonths = listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
            "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
            "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )

        val monthName = if (m in 1..12) hijriMonths[m - 1] else "محرم"
        return String.format(Locale("ar"), "%d %s %d هـ", d, monthName, y)
    }

    fun getNextPrayerInfo(prayerTimes: PrayerTimes, currentTimeMillis: Long = System.currentTimeMillis()): NextPrayerInfo {
        val now = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now.time)

        val prayersList = listOf(
            "الفجر" to prayerTimes.fajr,
            "الشروق" to prayerTimes.shorooq,
            "الظهر" to prayerTimes.dhuhr,
            "العصر" to prayerTimes.asr,
            "المغرب" to prayerTimes.maghrib,
            "العشاء" to prayerTimes.isha
        )

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        var nextPrayerName = "الفجر"
        var nextPrayerTimeMillis = 0L
        var foundNext = false

        for ((name, time) in prayersList) {
            try {
                val pDate = sdf.parse("$todayStr $time")
                if (pDate != null && pDate.time > currentTimeMillis) {
                    nextPrayerName = name
                    nextPrayerTimeMillis = pDate.time
                    foundNext = true
                    break
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (!foundNext) {
            // Next prayer is tomorrow's Fajr
            val tomorrow = Calendar.getInstance().apply {
                timeInMillis = currentTimeMillis
                add(Calendar.DAY_OF_YEAR, 1)
            }
            val tomorrowStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(tomorrow.time)
            try {
                val pDate = sdf.parse("$tomorrowStr ${prayerTimes.fajr}")
                if (pDate != null) {
                    nextPrayerName = "الفجر"
                    nextPrayerTimeMillis = pDate.time
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val remainingMillis = maxOf(0L, nextPrayerTimeMillis - currentTimeMillis)
        val remainingSecs = remainingMillis / 1000
        val hours = remainingSecs / 3600
        val mins = (remainingSecs % 3600) / 60
        val secs = remainingSecs % 60

        val formatted = String.format(Locale("ar"), "%02d:%02d:%02d", hours, mins, secs)
        val progressFraction = maxOf(0f, minOf(1f, 1f - (remainingMillis.toFloat() / (6 * 3600 * 1000f))))

        return NextPrayerInfo(
            nameAr = nextPrayerName,
            timeFormatted = formatted,
            remainingMillis = remainingMillis,
            progressFraction = progressFraction
        )
    }

    fun calculateQiblaBearing(latitude: Double, longitude: Double): Double {
        val meccaLat = Math.toRadians(21.4225)
        val meccaLng = Math.toRadians(39.8262)
        val userLat = Math.toRadians(latitude)
        val userLng = Math.toRadians(longitude)

        val dLng = meccaLng - userLng
        val y = sin(dLng) * cos(meccaLat)
        val x = cos(userLat) * sin(meccaLat) - sin(userLat) * cos(meccaLat) * cos(dLng)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0
        return bearing
    }
}
