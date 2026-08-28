package com.example

import com.example.model.Athlete
import com.example.model.YoYoProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testYoYoProtocolShuttlesCountAndMaxDistance() {
    assertEquals(91, YoYoProtocol.totalShuttlesCount)
    assertEquals(3640, YoYoProtocol.maxDistanceMeters)
  }

  @Test
  fun testInitialStageSpeedAndDistance() {
    val first = YoYoProtocol.shuttles.first()
    assertEquals(5, first.speedLevel)
    assertEquals(1, first.shuttleInLevel)
    assertEquals(10.0, first.speedKmh, 0.01)
    assertEquals(40, first.cumulativeDistanceMeters)
  }

  @Test
  fun testBangsboVo2MaxCalculation() {
    // Bangsbo formula: Distance (m) * 0.0084 + 36.4
    // For 1000m -> 1000 * 0.0084 + 36.4 = 8.4 + 36.4 = 44.8
    val vo2 = YoYoProtocol.calculateVo2Max(1000)
    assertEquals(44.8, vo2, 0.01)
  }

  @Test
  fun testDefaultAthleteRosterContainsRequestedNames() {
    val roster = Athlete.createDefaultRoster()
    val names = roster.map { it.name }
    val expected = listOf(
      "Silas", "Finley", "Arvid", "Lion", "Jakob", "Paul", "Lennox", "Levi",
      "Lasse", "Milan", "Lionel", "Arturo", "Peter", "Tommy", "Alex", "Tayo"
    )
    assertEquals(16, names.size)
    assertEquals(expected, names)
  }
}
