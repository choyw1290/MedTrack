package com.yeawon.s34598111.medtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import com.yeawon.s34598111.medtrack.data.MedTrackRepository
import com.yeawon.s34598111.medtrack.entity.Patient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


// ViewModel managing patient data and authentication
class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedTrackRepository

    val allPatients: StateFlow<List<Patient>>

    init{
        val database = MedTrackDatabase.Companion.getDatabase(application)
        repository = MedTrackRepository(
            application.applicationContext,
            database.patientDao(),
            database.medicationDao(),
            database.symptomDao()
        )
        allPatients = repository.getAllPatients()
            .stateIn(
                viewModelScope,
                SharingStarted.Companion.Lazily,
                emptyList()
            )
    }

    // Insert new patient into database
    fun insertPatient(patient: Patient) = viewModelScope.launch {
        repository.insertPatient(patient)
    }

    // Authenticates patient login using ID and password
    fun login(patientId: String, password: String, onResult: (Patient?) -> Unit) {
        viewModelScope.launch {
            val patient = repository.login(patientId, password)
            onResult(patient)
        }
    }

    // Check new patient information and if used information by existing patient
    // will be not success
    fun signUp(name: String, phone: String, password: String, onResult: (String, String?) -> Unit){
        viewModelScope.launch {
            val existing = repository.getPatientByPhone(phone)

            if(existing != null) {
                onResult("PHONE_EXISTS", null)
                return@launch
            }

            val newId = generatePatientId()

            val newPatient = Patient(
                patientId = newId,
                name = name,
                phoneNumber = phone,
                password = password
            )

            repository.insertPatient(newPatient)

            onResult("SUCCESS", newId)
        }
    }

    private suspend fun generatePatientId(): String {

        val lastId = repository.getMaxPatientId()

        var newId: String

        if(lastId == null) {
            newId = "P1001"
        } else {
            val numberPart = lastId.substring(1)
            val number = numberPart.toInt()

            val newNumber = number + 1

            newId = "P$newNumber"
        }

        return newId
    }

}