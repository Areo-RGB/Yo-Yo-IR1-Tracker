package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_sessions")
data class TestSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val durationSeconds: Long,
    val maxDistanceAchieved: Int,
    val maxLevelAchieved: String,
    val totalAthletesCount: Int,
    val completedAthletesCount: Int,
    val notes: String = ""
)
