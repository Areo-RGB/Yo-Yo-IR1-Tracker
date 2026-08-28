package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "athlete_results",
    foreignKeys = [
        ForeignKey(
            entity = TestSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class AthleteResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val athleteName: String,
    val finalDistanceMeters: Int,
    val finalLevel: String,
    val finalShuttleNumber: Int,
    val warningDistanceMeters: Int?,
    val warningLevel: String?,
    val rank: Int,
    val vo2Max: Double
)
