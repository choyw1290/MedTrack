package com.yeawon.s34598111.medtrack.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yeawon.s34598111.medtrack.entity.Medication
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    // Insert medication
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(medication: Medication)

    // Retrieve all medications
    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flow<List<Medication>>

    // Get medication from a specific patient
    @Query("SELECT * FROM medications WHERE patientId = :patientId")
    fun getMedicationByPatient(patientId: String): Flow<List<Medication>>

    // Count total number of medications for a specific patient
    @Query("SELECT COUNT(*) FROM medications WHERE patientId = :patientId")
    suspend fun getMedicationCount(patientId: String): Int
}