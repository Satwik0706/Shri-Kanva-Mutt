package com.satwik.example.mutt_app.data

import java.util.*
import kotlin.math.*

/**
 * High-Precision Astronomical Panchanga Engine.
 * Implements Amanta (South Indian / Karnataka) Panchanga rules.
 * All calendar events are derived dynamically from physical celestial coordinates.
 */
object PanchangaEngine {

    // 12 Soura Masas corresponding to entering specific solar signs (Rashis)
    val SOURA_MASA_NAMES = listOf(
        "Mesha", "Vrishabha", "Mithuna", "Karka", "Simha", "Kanya",
        "Tula", "Vrischika", "Dhanu", "Makara", "Kumbha", "Meena"
    )

    // 12 Standard Lunar Months
    val LUNAR_MONTH_NAMES = listOf(
        "Chaitra", "Vaishakha", "Jyeshtha", "Ashadha", "Shravana", "Bhadrapada",
        "Ashwin", "Kartika", "Margashirsha", "Pausha", "Magha", "Phalguna"
    )

    // 60 Samvatsara names
    val SAMVATSARA_NAMES = listOf(
        "Prabhava", "Vibhava", "Shukla", "Pramoduta", "Prajapati", "Angirasa", "Shrimukha", "Bhava", "Yuva", "Dhata",
        "Ishvara", "Bahudhanya", "Pramathi", "Vikrama", "Vrisha", "Chitrabhanu", "Subhanu", "Tarana", "Parthiva", "Vyaya",
        "Sarvajit", "Sarvadhari", "Virodhi", "Vikriti", "Khara", "Nandana", "Vijaya", "Jaya", "Manmatha", "Durmukhi",
        "Hevilambi", "Vilambi", "Vikari", "Sharvari", "Plava", "Shubhakrit", "Shobhakrit", "Krodhi", "Vishwavasu", "Parabhava",
        "Plavanga", "Kilaka", "Saumya", "Sadharana", "Virodhikrit", "Paridhavi", "Pramadi", "Ananda", "Rakshasa", "Nala",
        "Pingala", "Kalayukta", "Siddharthi", "Raudra", "Durmati", "Dundubhi", "Rudhirodgari", "Raktakshi", "Krodhana", "Akshaya"
    )

    data class DetailedResult(
        val samvatsara: String,
        val ayana: String,
        val ritu: String,
        val masa: String,
        val paksha: String,
        val tithi: String,
        val tithiStart: String,
        val tithiEnd: String,
        val nextTithi: String,
        val nakshatra: String,
        val nakshatraStart: String,
        val nakshatraEnd: String,
        val nextNakshatra: String,
        val yoga: String,
        val karana: String,
        val souraMasa: String,
        val rahuKala: String,
        val yamaganda: String,
        val suryodaya: String,
        val suryasta: String,
        val note: String
    )

    /**
     * Standard Lahiri Ayanamsa computation.
     * Precession of equinoxes is approximately 5025.64 seconds per century since epoch J2000.
     */
    private fun getAyanamsa(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        return 23.85 + (5025.64 / 3600.0) * t
    }

    private fun getJulianDate(t: Long): Double = (t / 86400000.0) + 2440587.5

    /**
     * Sidereal longitude of the Sun (Ayanamsa subtracted for Nirayana system).
     */
    private fun getSiderealSun(jd: Double, ayanamsa: Double): Double {
        val d = jd - 2451545.0
        val g = Math.toRadians((357.529 + 0.98560028 * d) % 360)
        val q = (280.459 + 0.98564736 * d) % 360
        val l = (q + 1.915 * sin(g) + 0.020 * sin(2 * g)) % 360
        return (l - ayanamsa + 360) % 360
    }

    /**
     * Sidereal longitude of the Moon (Ayanamsa subtracted for Nirayana system).
     */
    private fun getSiderealMoon(jd: Double, ayanamsa: Double): Double {
        val d = jd - 2451545.0
        val l = (218.316 + 13.176396 * d) % 360
        val m = Math.toRadians((134.963 + 13.064993 * d) % 360)
        val mPrime = Math.toRadians((357.529 + 0.9856003 * d) % 360)
        val elongation = Math.toRadians((297.850 + 12.190749 * d) % 360)
        val longitude = l + 6.289 * sin(m) + 1.274 * sin(2 * elongation - m) + 0.658 * sin(2 * elongation) + 0.214 * sin(2 * m) - 0.186 * sin(mPrime)
        return (longitude - ayanamsa + 360) % 360
    }

    /**
     * Finds the exact Julian Date of the closest Amavasya (conjunction, elongation = 0) near jdApprox.
     */
    private fun findAmavasya(jdApprox: Double, ayanamsa: Double): Double {
        var estimate = jdApprox
        repeat(6) {
            val moon = getSiderealMoon(estimate, ayanamsa)
            val sun = getSiderealSun(estimate, ayanamsa)
            val diff = (moon - sun + 360) % 360
            val error = if (diff > 180.0) diff - 360.0 else diff
            val correction = error / 12.19075
            estimate -= correction
        }
        return estimate
    }

    /**
     * Checks which solar boundaries (Sankrantis) the Sun crosses during the interval [jd1, jd2].
     * Returns a list of Rashi indices (0 to 11) corresponding to the entered Rashis.
     */
    private fun getSankrantis(jd1: Double, jd2: Double, ayanamsa: Double): List<Int> {
        val sun1 = getSiderealSun(jd1, ayanamsa)
        val sun2 = getSiderealSun(jd2, ayanamsa)
        var delta = sun2 - sun1
        while (delta < 0) delta += 360.0
        while (delta >= 360) delta -= 360.0
        
        val list = mutableListOf<Int>()
        for (rashi in 0..11) {
            val boundary = rashi * 30.0
            val shiftedBoundary = (boundary - sun1 + 360) % 360
            if (shiftedBoundary > 0.0 && shiftedBoundary <= delta) {
                list.add(rashi)
            }
        }
        return list
    }

    data class LunarMonthInfo(val name: String, val index: Int, val type: String)

    /**
     * Computes the name, index, and type of the lunar month defined by the interval [amavasyaStart, amavasyaEnd].
     */
    private fun getLunarMonthInfo(amavasyaStart: Double, amavasyaEnd: Double, ayanamsa: Double): LunarMonthInfo {
        val sankrantis = getSankrantis(amavasyaStart, amavasyaEnd, ayanamsa)
        return when {
            sankrantis.isEmpty() -> {
                // Adhika Masa: No solar transit happens inside this lunar month.
                // The Sun's rashi at both start and end remains the same.
                val sunStart = getSiderealSun(amavasyaStart, ayanamsa)
                val rashi = (sunStart / 30.0).toInt() % 12
                val nextRashi = (rashi + 1) % 12
                LunarMonthInfo("Adhika ${LUNAR_MONTH_NAMES[nextRashi]}", nextRashi, "adhika")
            }
            sankrantis.size == 1 -> {
                // Normal month: Exactly one transit occurs.
                val rashi = sankrantis[0]
                LunarMonthInfo(LUNAR_MONTH_NAMES[rashi], rashi, "normal")
            }
            else -> {
                // Kshaya Masa: Two transits occur.
                val rashi = sankrantis[0]
                LunarMonthInfo("Kshaya ${LUNAR_MONTH_NAMES[rashi]}", rashi, "kshaya")
            }
        }
    }

    /**
     * Standard solar position algorithm with high accuracy refraction correction.
     */
    private fun calculatePreciseSunTimes(t: Long, lat: Double, lon: Double): SunTimes {
        val tz = TimeZone.getDefault()
        val cal = Calendar.getInstance(tz).apply {
            timeInMillis = t
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val jd12 = getJulianDate(cal.timeInMillis)
        val d = jd12 - 2451545.0
        val latRad = Math.toRadians(lat)
        
        val M = Math.toRadians((357.5291 + 0.98560028 * d) % 360)
        val C = 1.9148 * sin(M) + 0.02 * sin(2 * M) + 0.0003 * sin(3 * M)
        val lambda = Math.toRadians((Math.toDegrees(M) + C + 102.9372 + 180.0) % 360)
        val obliquity = Math.toRadians(23.44)
        val declination = asin(sin(lambda) * sin(obliquity))
        
        val B = Math.toRadians(360.0 * (d - 81.0) / 365.0)
        val eot = 9.87 * sin(2 * B) - 7.53 * cos(B) - 1.5 * sin(B)
        
        val cosH = (sin(Math.toRadians(-0.833)) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))
        val H = if (cosH < -1.0) 180.0 else if (cosH > 1.0) 0.0 else Math.toDegrees(acos(cosH))
        
        val noonUtc = 12.0 - (lon / 15.0) - (eot / 60.0)
        val riseUtc = noonUtc - (H / 15.0)
        val setUtc = noonUtc + (H / 15.0)
        
        val calStart = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = cal.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val baseUtcMs = calStart.timeInMillis
        return SunTimes(
            baseUtcMs + (riseUtc * 3600000.0).toLong(),
            baseUtcMs + (setUtc * 3600000.0).toLong()
        )
    }

    private data class SunTimes(val rise: Long, val set: Long)

    /**
     * Highly precise root finder using a bounded bisection algorithm to find 
     * the exact transition time for Tithis and Nakshatras.
     */
    private fun findTransitionTime(startTs: Long, targetAngle: Double, isTithi: Boolean, ayanamsa: Double): Long {
        val stepMs = 3600000L // 1 hour steps to bracket the crossing
        var tLeft = startTs
        var tRight = startTs + stepMs
        
        fun getAngleAt(ts: Long): Double {
            val jd = getJulianDate(ts)
            val moon = getSiderealMoon(jd, ayanamsa)
            return if (isTithi) {
                val sun = getSiderealSun(jd, ayanamsa)
                (moon - sun + 360) % 360
            } else {
                moon
            }
        }
        
        var found = false
        repeat(28) {
            val aLeft = getAngleAt(tLeft)
            val aRight = getAngleAt(tRight)
            
            val shiftedRight = (aRight - targetAngle + 360) % 360
            val shiftedLeft = (aLeft - targetAngle + 360) % 360
            
            if (shiftedLeft > 340.0 && shiftedRight < 20.0) {
                found = true
                return@repeat
            }
            
            tLeft = tRight
            tRight += stepMs
        }
        
        if (!found) {
            return startTs + 86400000L
        }
        
        var low = tLeft
        var high = tRight
        repeat(15) {
            val mid = (low + high) / 2
            val aMid = getAngleAt(mid)
            val shiftedMid = (aMid - targetAngle + 360) % 360
            if (shiftedMid < 180.0) {
                high = mid
            } else {
                low = mid
            }
        }
        return (low + high) / 2
    }

    private fun getTithiName(tithiNum: Int): String {
        val num = ((tithiNum - 1) % 30 + 30) % 30 + 1
        val names = listOf(
            "Prathama", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
            "Shashti", "Saptami", "Ashtami", "Navami", "Dashami",
            "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi"
        )
        return when (num) {
            15 -> "Shukla Purnima"
            30 -> "Krishna Amavasya"
            else -> {
                val isShukla = num < 15
                val label = if (isShukla) "Shukla" else "Krishna"
                val idx = if (isShukla) num - 1 else num - 16
                "$label ${names[idx]}"
            }
        }
    }

    private fun getNakshatra(moon: Double): String {
        val idx = (moon / (360.0/27.0)).toInt() % 27
        val pada = ((moon % (360.0/27.0)) / (360.0/108.0)).toInt() + 1
        return "${getNakshatraFromIdx(idx)} ($pada)"
    }

    private fun getNakshatraFromIdx(idx: Int): String {
        val names = listOf(
            "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashirsha", "Ardra", "Punarvasu", "Pushya", "Ashlesha", "Magha",
            "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha", "Mula", "Purva Ashadha",
            "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
        )
        return names[(idx % 27 + 27) % 27]
    }

    private fun getYoga(sun: Double, moon: Double): String {
        val names = listOf(
            "Vishkumbha", "Preeti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda", "Sukarma", "Dhriti", "Shoola", "Ganda",
            "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra", "Siddhi", "Vyatipata", "Variyan", "Parigha", "Shiva",
            "Siddha", "Sadhya", "Shubha", "Shukla", "Brahma", "Indra", "Vaidhriti"
        )
        return names[((sun + moon) % 360 / (360.0/27.0)).toInt() % 27]
    }

    private fun getKarana(sun: Double, moon: Double): String {
        val fixed = listOf("Shakuni", "Chatushpada", "Naga", "Kimstughna")
        val repeating = listOf("Bava", "Balava", "Kaulava", "Taitila", "Garaja", "Vanija", "Vishti")
        val kIndex = ((moon - sun + 360) % 360 / 6.0).toInt()
        return when {
            kIndex == 0 -> "Kimstughna"
            kIndex >= 57 -> fixed[(kIndex - 57) % 4]
            else -> repeating[(kIndex - 1) % 7]
        }
    }

    private fun getRitu(lunarMonthIdx: Int): String {
        val ritus = listOf("Vasanta", "Grishma", "Varsha", "Sharad", "Hemanta", "Shishira")
        return ritus[(lunarMonthIdx % 12) / 2]
    }

    fun calculate(timestamp: Long, lat: Double = 12.9716, lon: Double = 77.5946): DetailedResult {
        // Step 1: Calculate precise Sunrise and Sunset
        val sunTimes = calculatePreciseSunTimes(timestamp, lat, lon)
        val sunriseTs = sunTimes.rise
        val jd = getJulianDate(sunriseTs)
        val ayanamsa = getAyanamsa(jd)
        
        // Step 2: Sidereal positions of Sun and Moon at Sunrise
        val sidSun = getSiderealSun(jd, ayanamsa)
        val sidMoon = getSiderealMoon(jd, ayanamsa)
        
        // Step 3: Enclosing Amavasyas to determine Lunar Month dynamically
        val diff = (sidMoon - sidSun + 360) % 360
        var amavasyaStart = findAmavasya(jd - diff / 12.19075, ayanamsa)
        if (amavasyaStart > jd) {
            amavasyaStart = findAmavasya(amavasyaStart - 29.53, ayanamsa)
        }
        var amavasyaEnd = findAmavasya(amavasyaStart + 29.53, ayanamsa)
        if (amavasyaEnd < jd) {
            amavasyaStart = amavasyaEnd
            amavasyaEnd = findAmavasya(amavasyaStart + 29.53, ayanamsa)
        }
        
        // Step 4: Lunar Month Info
        val masaInfo = getLunarMonthInfo(amavasyaStart, amavasyaEnd, ayanamsa)
        
        // Step 5: High-Precision Samvatsara (60-year cycle) based on Shaka year
        // We find the starting Amavasya of Chaitra (month index 0) for the current lunar year.
        val jdChaitra = amavasyaStart - masaInfo.index * 29.53059
        val amavasyaChaitra = findAmavasya(jdChaitra, ayanamsa)
        val calChaitra = Calendar.getInstance().apply { timeInMillis = ((amavasyaChaitra - 2440587.5) * 86400000.0).toLong() }
        val chaitraYear = calChaitra.get(Calendar.YEAR)
        val shakaYear = chaitraYear - 78
        val samvatsaraIdx = ((shakaYear - 1947 + 38) % 60 + 60) % 60
        val samvatsara = SAMVATSARA_NAMES[samvatsaraIdx]
        
        // Step 6: Tithi (elongation is divided into 30 parts of 12 degrees each)
        val tithiNum = (diff / 12.0).toInt() + 1
        val nextTithiAngle = (tithiNum * 12.0) % 360
        val tithiEndTs = findTransitionTime(sunriseTs, nextTithiAngle, true, ayanamsa)
        
        val prevTithiAngle = ((tithiNum - 1) * 12.0 + 360) % 360
        val tithiStartTs = findTransitionTime(sunriseTs - 86400000L, prevTithiAngle, true, ayanamsa)
        
        // Step 7: Nakshatra (moon longitude is divided into 27 parts of 13.333 degrees each)
        val nakIdx = (sidMoon / (360.0/27.0)).toInt() % 27
        val nextNakAngle = ((nakIdx + 1) * (360.0/27.0)) % 360
        val nakEndTs = findTransitionTime(sunriseTs, nextNakAngle, false, ayanamsa)
        
        val prevNakAngle = (nakIdx * (360.0/27.0) + 360) % 360
        val nakStartTs = findTransitionTime(sunriseTs - 86400000L, prevNakAngle, false, ayanamsa)
        
        // Step 8: Ayana & Soura Masa (based on Sun's Nirayana longitude)
        val sunRashiIdx = (sidSun / 30.0).toInt() % 12
        val realAyana = if (sidSun >= 270.0 || sidSun < 90.0) "Uttarayana" else "Dakshinayana"
        val souraMasa = SOURA_MASA_NAMES[sunRashiIdx]
        
        // Step 9: Paksha & Ritu
        val isShukla = tithiNum <= 15
        val ritu = getRitu(masaInfo.index)
        
        // Step 10: Segments for Rahu Kala & Yamaganda
        val segments = calculateSegments(sunTimes.rise, sunTimes.set, timestamp)
        
        return DetailedResult(
            samvatsara = samvatsara,
            ayana = realAyana,
            ritu = ritu,
            masa = masaInfo.name,
            paksha = if (isShukla) "Shukla" else "Krishna",
            tithi = getTithiName(tithiNum),
            tithiStart = formatTime(tithiStartTs),
            tithiEnd = formatTime(tithiEndTs),
            nextTithi = getTithiName(tithiNum + 1),
            nakshatra = getNakshatra(sidMoon),
            nakshatraStart = formatTime(nakStartTs),
            nakshatraEnd = formatTime(nakEndTs),
            nextNakshatra = getNakshatraFromIdx(nakIdx + 1),
            yoga = getYoga(sidSun, sidMoon),
            karana = getKarana(sidSun, sidMoon),
            souraMasa = souraMasa,
            rahuKala = segments.rahu,
            yamaganda = segments.yamaganda,
            suryodaya = formatTime(sunTimes.rise),
            suryasta = formatTime(sunTimes.set),
            note = "High Precision Engine (Ayanamsa: ${String.format(Locale.getDefault(), "%.3f", ayanamsa)}°)"
        )
    }

    private fun calculateSegments(rise: Long, set: Long, t: Long): Segments {
        val duration = set - rise
        val seg = duration / 8
        val day = Calendar.getInstance().apply { timeInMillis = t }.get(Calendar.DAY_OF_WEEK)
        val rahuIdx = mapOf(1 to 8, 2 to 2, 3 to 7, 4 to 5, 5 to 6, 6 to 4, 7 to 3)[day] ?: 0
        val yamIdx = mapOf(1 to 5, 2 to 4, 3 to 3, 4 to 2, 5 to 1, 6 to 7, 7 to 6)[day] ?: 0
        fun format(idx: Int) = formatTime(rise + (idx-1)*seg) + " - " + formatTime(rise + idx*seg)
        return Segments(format(rahuIdx), format(yamIdx))
    }

    private data class Segments(val rahu: String, val yamaganda: String)

    private fun formatTime(t: Long) = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(t))
}