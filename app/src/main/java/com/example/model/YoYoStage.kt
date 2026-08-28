package com.example.model

/**
 * Yo-Yo Intermittent Recovery Test Level 1 (Yo-Yo IR1) Protocol Definition.
 *
 * Official Protocol Specifications:
 * - 2 x 20m shuttle runs with 10s active recovery (2 x 5m walk/jog) between shuttles.
 * - Speeds start at 10.0 km/h and increase systematically.
 * - Distance per shuttle = 40 meters.
 * - Running duration per shuttle = 40m / (Speed in m/s) = 144 / Speed_kmh.
 * - Recovery duration per shuttle = 10.0 seconds.
 */
data class YoYoShuttle(
    val shuttleNumber: Int,          // 1 to 91
    val speedLevel: Int,             // e.g. 5, 9, 11, 12, 13, 14, ... 23
    val shuttleInLevel: Int,         // 1, 2, ...
    val speedKmh: Double,            // 10.0, 11.5, 12.0, ...
    val cumulativeDistanceMeters: Int, // (shuttleNumber) * 40
    val runDurationSeconds: Double,  // 144.0 / speedKmh
    val recoveryDurationSeconds: Double = 10.0
) {
    val levelDisplay: String = "$speedLevel.$shuttleInLevel"
    val totalDurationSeconds: Double = runDurationSeconds + recoveryDurationSeconds
}

object YoYoProtocol {
    /**
     * Complete Yo-Yo IR1 Protocol Shuttles.
     * Speed stages:
     * - Level 5: 10.0 km/h, 1 shuttle (40m)
     * - Level 9: 11.5 km/h, 1 shuttle (80m)
     * - Level 11: 12.0 km/h, 2 shuttles (120m, 160m)
     * - Level 12: 12.5 km/h, 3 shuttles (200m, 240m, 280m)
     * - Level 13: 13.0 km/h, 4 shuttles (320m, 360m, 400m, 440m)
     * - Level 14: 13.5 km/h, 8 shuttles (480m to 760m)
     * - Level 15: 14.0 km/h, 8 shuttles (800m to 1080m)
     * - Level 16: 14.5 km/h, 8 shuttles (1120m to 1400m)
     * - Level 17: 15.0 km/h, 8 shuttles (1440m to 1720m)
     * - Level 18: 15.5 km/h, 8 shuttles (1760m to 2040m)
     * - Level 19: 16.0 km/h, 8 shuttles (2080m to 2360m)
     * - Level 20: 16.5 km/h, 8 shuttles (2400m to 2680m)
     * - Level 21: 17.0 km/h, 8 shuttles (2720m to 3000m)
     * - Level 22: 17.5 km/h, 8 shuttles (3040m to 3320m)
     * - Level 23: 18.0 km/h, 8 shuttles (3360m to 3640m)
     */
    val shuttles: List<YoYoShuttle> = buildList {
        val stages = listOf(
            StageConfig(level = 5, speedKmh = 10.0, shuttleCount = 1),
            StageConfig(level = 9, speedKmh = 11.5, shuttleCount = 1),
            StageConfig(level = 11, speedKmh = 12.0, shuttleCount = 2),
            StageConfig(level = 12, speedKmh = 12.5, shuttleCount = 3),
            StageConfig(level = 13, speedKmh = 13.0, shuttleCount = 4),
            StageConfig(level = 14, speedKmh = 13.5, shuttleCount = 8),
            StageConfig(level = 15, speedKmh = 14.0, shuttleCount = 8),
            StageConfig(level = 16, speedKmh = 14.5, shuttleCount = 8),
            StageConfig(level = 17, speedKmh = 15.0, shuttleCount = 8),
            StageConfig(level = 18, speedKmh = 15.5, shuttleCount = 8),
            StageConfig(level = 19, speedKmh = 16.0, shuttleCount = 8),
            StageConfig(level = 20, speedKmh = 16.5, shuttleCount = 8),
            StageConfig(level = 21, speedKmh = 17.0, shuttleCount = 8),
            StageConfig(level = 22, speedKmh = 17.5, shuttleCount = 8),
            StageConfig(level = 23, speedKmh = 18.0, shuttleCount = 8)
        )

        var globalShuttleNum = 1
        for (stage in stages) {
            for (shuttleInLevel in 1..stage.shuttleCount) {
                add(
                    YoYoShuttle(
                        shuttleNumber = globalShuttleNum,
                        speedLevel = stage.level,
                        shuttleInLevel = shuttleInLevel,
                        speedKmh = stage.speedKmh,
                        cumulativeDistanceMeters = globalShuttleNum * 40,
                        runDurationSeconds = 144.0 / stage.speedKmh,
                        recoveryDurationSeconds = 10.0
                    )
                )
                globalShuttleNum++
            }
        }
    }

    val totalShuttlesCount: Int = shuttles.size // 91
    val maxDistanceMeters: Int = shuttles.last().cumulativeDistanceMeters // 3640m

    fun getCumulativeTimeUpToShuttleMs(shuttleIndex: Int): Long {
        var totalSec = 0.0
        for (i in 0 until shuttleIndex.coerceIn(0, shuttles.size)) {
            totalSec += shuttles[i].totalDurationSeconds
        }
        return (totalSec * 1000).toLong()
    }

    /**
     * Bangsbo formula for Yo-Yo IR1 VO2max estimation:
     * VO2max (mL/kg/min) = IR1 distance (m) × 0.0084 + 36.4
     */
    fun calculateVo2Max(distanceMeters: Int): Double {
        if (distanceMeters <= 0) return 0.0
        val vo2 = (distanceMeters * 0.0084) + 36.4
        return (vo2 * 10.0).toInt() / 10.0 // 1 decimal place
    }

    /**
     * Fitness rating category based on distance for athletes.
     */
    fun getFitnessRating(distanceMeters: Int): String {
        return when {
            distanceMeters >= 2400 -> "Elite (Professional)"
            distanceMeters >= 2000 -> "Excellent"
            distanceMeters >= 1600 -> "Good / Advanced"
            distanceMeters >= 1200 -> "Average / Intermediate"
            distanceMeters >= 800 -> "Below Average"
            else -> "Novice / Needs Improvement"
        }
    }

    private data class StageConfig(
        val level: Int,
        val speedKmh: Double,
        val shuttleCount: Int
    )
}
