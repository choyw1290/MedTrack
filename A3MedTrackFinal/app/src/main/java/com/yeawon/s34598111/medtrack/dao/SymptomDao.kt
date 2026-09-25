package com.yeawon.s34598111.medtrack.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yeawon.s34598111.medtrack.entity.Symptom
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {

    // Insert symptom
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(symptom: Symptom)

    // Get all symptoms
    @Query("SELECT * FROM symptoms")
    fun getAllSymptoms(): Flow<List<Symptom>>

    // Get symptoms for specific patient
    @Query("SELECT * FROM symptoms WHERE patientId = :patientId")
    fun getSymptomsByPatient(patientId: String): Flow<List<Symptom>>

    // Get most frequently recorded symptom category
    @Query("""SELECT category FROM SYMPTOMS
        GROUP BY category
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    suspend fun getMostCommonSymptom(): String?

    // Get average symptom severity
    @Query("SELECT AVG(severity) FROM symptoms")
    suspend fun getAverageSeverity(): Double?
}