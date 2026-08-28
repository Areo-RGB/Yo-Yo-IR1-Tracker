package com.example.data.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.data.entity.AthleteResultEntity
import com.example.data.entity.TestSessionEntity
import kotlinx.coroutines.flow.Flow

data class SessionWithResults(
    @Embedded val session: TestSessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val results: List<AthleteResultEntity>
)

@Dao
interface TestSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TestSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<AthleteResultEntity>)

    @Transaction
    suspend fun saveCompleteSession(
        session: TestSessionEntity,
        results: List<AthleteResultEntity>
    ): Long {
        val sessionId = insertSession(session)
        val mappedResults = results.map { it.copy(sessionId = sessionId) }
        insertResults(mappedResults)
        return sessionId
    }

    @Transaction
    @Query("SELECT * FROM test_sessions ORDER BY timestampMs DESC")
    fun getAllSessionsWithResults(): Flow<List<SessionWithResults>>

    @Transaction
    @Query("SELECT * FROM test_sessions WHERE id = :sessionId")
    suspend fun getSessionWithResultsById(sessionId: Long): SessionWithResults?

    @Query("DELETE FROM test_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("DELETE FROM test_sessions")
    suspend fun deleteAllSessions()
}
