package com.example.data.repository

import com.example.data.dao.SessionWithResults
import com.example.data.dao.TestSessionDao
import com.example.data.entity.AthleteResultEntity
import com.example.data.entity.TestSessionEntity
import kotlinx.coroutines.flow.Flow

class YoYoRepository(private val dao: TestSessionDao) {
    val allSessions: Flow<List<SessionWithResults>> = dao.getAllSessionsWithResults()

    suspend fun saveSession(
        session: TestSessionEntity,
        results: List<AthleteResultEntity>
    ): Long {
        return dao.saveCompleteSession(session, results)
    }

    suspend fun getSessionById(sessionId: Long): SessionWithResults? {
        return dao.getSessionWithResultsById(sessionId)
    }

    suspend fun deleteSession(sessionId: Long) {
        dao.deleteSessionById(sessionId)
    }
}
