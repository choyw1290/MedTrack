package com.yeawon.s34598111.medtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import com.yeawon.s34598111.medtrack.data.MedTrackRepository
import kotlinx.coroutines.launch

// ViewModel responsible for verifying and handling account claiming
class ClaimAccountViewModel(application: Application): AndroidViewModel(application) {

    private val repository: MedTrackRepository

    init{
        val database = MedTrackDatabase.Companion.getDatabase(application)
        repository = MedTrackRepository(
            application.applicationContext,
            database.patientDao(),
            database.medicationDao(),
            database.symptomDao()
        )
    }

    // Validate patient ID and phone number, then updates the password if both match patient data
    // Returns a result indicating success or type of validation failure
    fun claimAccount(patientId: String, phoneNumber: String, password: String, onResult: (Boolean, String) -> Unit){
        viewModelScope.launch {
            val patientById = repository.getPatientById(patientId)
            val patientByPhone = repository.getPatientByPhone(phoneNumber)

            if(patientById == null) {
                onResult(false, "INVALID_ID")
                return@launch
            }

            if(patientByPhone == null) {
                onResult(false, "INVALID_PHONE")
                return@launch
            }

            if(patientById.patientId != patientByPhone.patientId) {
                onResult(false, "MISMATCH")
                return@launch
            }

            repository.updatePassword(patientId,password)

            onResult(true, "SUCCESS")
        }
    }

}