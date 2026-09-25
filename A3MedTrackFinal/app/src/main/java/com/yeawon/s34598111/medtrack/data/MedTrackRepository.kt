package com.yeawon.s34598111.medtrack.data

import android.content.Context
import com.google.gson.Gson
import com.yeawon.s34598111.medtrack.dao.MedicationDao
import com.yeawon.s34598111.medtrack.dao.PatientDao
import com.yeawon.s34598111.medtrack.dao.SymptomDao
import com.yeawon.s34598111.medtrack.entity.Medication
import com.yeawon.s34598111.medtrack.entity.Patient
import com.yeawon.s34598111.medtrack.entity.Symptom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MedTrackRepository(private val context: Context, private val patientDao: PatientDao, private val medicationDao: MedicationDao, private val symptomDao: SymptomDao) {

    // ------ Patient ------
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    suspend fun insertPatient(patient: Patient){ patientDao.insert(patient) }

    suspend fun getPatientByPhone(phone: String): Patient? {
        return patientDao.getPatientByPhone(phone)
    }

    suspend fun getPatientById(patientId: String): Patient? {
        return patientDao.getPatientById(patientId)
    }

    suspend fun getMaxPatientId(): String? {
        return patientDao.getMaxPatientId()
    }

    suspend fun getAllPatientsList(): List<Patient> {
        return patientDao.getAllPatients().first()
    }

    suspend fun updatePassword(patientId: String, password: String) {
        return patientDao.updatePatient(patientId, password)
    }

    suspend fun login(phone: String, password: String): Patient? { return patientDao.login(phone, password) }

    // ------ Medication ------
    fun getAllMedications(): Flow<List<Medication>> = medicationDao.getAllMedications()
    suspend fun insertMedication(medication: Medication){ medicationDao.insert(medication) }

    fun getMedicationByPatient(patientId: String): Flow<List<Medication>> { return medicationDao.getMedicationByPatient(patientId) }

    suspend fun getMedicationCount(patientId: String): Int{
        return medicationDao.getMedicationCount(patientId)
    }
    // ------ Symptom ------
    fun getAllSymptoms(): Flow<List<Symptom>> = symptomDao.getAllSymptoms()
    suspend fun insertSymptom(symptom: Symptom) { symptomDao.insert(symptom) }

    fun getSymptomByPatient(patientId: String): Flow<List<Symptom>>{ return symptomDao.getSymptomsByPatient(patientId) }

    suspend fun getMostCommonSymptom(): String? {
        return symptomDao.getMostCommonSymptom()
    }

    suspend fun getAverageSeverity(): Double? {
        return symptomDao.getAverageSeverity()
    }
}