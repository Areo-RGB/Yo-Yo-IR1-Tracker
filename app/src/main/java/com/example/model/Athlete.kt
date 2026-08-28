package com.example.model

enum class AthleteStatus {
    RUNNING,
    WARNED,
    ELIMINATED
}

enum class ShuttlePhase {
    RUNNING,     // 40m running sprint phase
    RECOVERY     // 10s active recovery jog phase
}

enum class TestState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

data class Athlete(
    val id: String,
    val name: String,
    val status: AthleteStatus = AthleteStatus.RUNNING,
    val warningDistanceMeters: Int? = null,
    val warningLevel: String? = null,
    val warningShuttle: Int? = null,
    val warningTimestampMs: Long? = null,
    val finalDistanceMeters: Int? = null,
    val finalLevel: String? = null,
    val finalShuttle: Int? = null,
    val finishTimestampMs: Long? = null,
    val rank: Int? = null,
    val vo2Max: Double? = null
) {
    val isFinished: Boolean get() = status == AthleteStatus.ELIMINATED
    val isWarned: Boolean get() = status == AthleteStatus.WARNED
    val isRunning: Boolean get() = status == AthleteStatus.RUNNING

    companion object {
        val DEFAULT_ATHLETE_NAMES = listOf(
            "Silas",
            "Finley",
            "Arvid",
            "Lion",
            "Jakob",
            "Paul",
            "Lennox",
            "Levi",
            "Lasse",
            "Milan",
            "Lionel",
            "Arturo",
            "Peter",
            "Tommy",
            "Alex",
            "Tayo"
        )

        fun createDefaultRoster(): List<Athlete> {
            return DEFAULT_ATHLETE_NAMES.mapIndexed { index, name ->
                Athlete(
                    id = "athlete_${index + 1}",
                    name = name,
                    status = AthleteStatus.RUNNING
                )
            }
        }
    }
}
