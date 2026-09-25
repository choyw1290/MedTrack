package com.yeawon.s34598111.medtrack.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yeawon.s34598111.medtrack.entity.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    // Insert a new patient
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(patient: Patient)

    // Validate login using patient ID and password
    @Query("SELECT * FROM patients WHERE patientId = :patientId AND password = :password")
    suspend fun login(patientId: String, password: String): Patient?

    // Check patient identity for account claim
    @Query("SELECT * FROM patients WHERE patientId = :patientId AND phoneNumber = :phone")
    suspend fun getPatientForClaim(patientId: String, phone: String): Patient?

    // Update patient password by patient ID
    @Query("Update patients SET password = :password WHERE patientId = :patientId")
    suspend fun updatePatient(patientId: String, password: String)

    // Get all patients
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    // Find patient by phone number
    @Query("SELECT * FROM patients WHERE phoneNumber = :phone")
    suspend fun getPatientByPhone(phone: String): Patient?

    // Find patient by patient ID
    @Query("SELECT * FROM patients WHERE patientId = :patientId")
    suspend fun getPatientById(patientId: String): Patient?

    // Get the highest patient ID for generating new IDs
    @Query("SELECT patientId FROM patients ORDER BY patientId DESC")
    suspend fun getMaxPatientId(): String?

    // Get total number of patients
    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getTotalPatients(): Int
}