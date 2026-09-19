package com.satwik.example.mutt_app

import com.satwik.example.mutt_app.data.PanchangaEngine
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class ExampleUnitTest {

    @Test
    fun testPanchangaCalculationForAug2026() {
        // August 28, 2026 at 12:00:00 Local Time
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 28, 12, 0, 0)
        }
        val timestamp = cal.timeInMillis
        val result = PanchangaEngine.calculate(timestamp)

        println("=== Panchanga for Aug 28, 2026 ===")
        println("Samvatsara: ${result.samvatsara}")
        println("Masa: ${result.masa}")
        println("Paksha: ${result.paksha}")
        println("Tithi: ${result.tithi}")
        println("Tithi End: ${result.tithiEnd}")
        println("Nakshatra: ${result.nakshatra}")
        println("Nakshatra End: ${result.nakshatraEnd}")
        println("Ayana: ${result.ayana}")
        println("Ritu: ${result.ritu}")
        println("Soura Masa: ${result.souraMasa}")
        println("Sunrise: ${result.suryodaya}")
        println("Sunset: ${result.suryasta}")
        println("Rahu Kala: ${result.rahuKala}")
        println("Yamaganda: ${result.yamaganda}")
        println("Yoga: ${result.yoga}")
        println("Karana: ${result.karana}")

        assertNotNull(result.samvatsara)
        assertNotNull(result.masa)
        assertNotNull(result.tithi)
        assertNotNull(result.nakshatra)
    }

    @Test
    fun testPanchangaCalculationForJan2026() {
        // January 15, 2026
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 15, 12, 0, 0)
        }
        val timestamp = cal.timeInMillis
        val result = PanchangaEngine.calculate(timestamp)

        println("=== Panchanga for Jan 15, 2026 ===")
        println("Samvatsara: ${result.samvatsara}")
        println("Masa: ${result.masa}")
        println("Tithi: ${result.tithi}")
        println("Nakshatra: ${result.nakshatra}")
        println("Ayana: ${result.ayana}")
        println("Ritu: ${result.ritu}")
        println("Soura Masa: ${result.souraMasa}")
        println("Sunrise: ${result.suryodaya}")
        println("Sunset: ${result.suryasta}")

        assertNotNull(result.samvatsara)
        assertNotNull(result.masa)
    }
}