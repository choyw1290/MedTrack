package com.yeawon.s34598111.medtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yeawon.s34598111.medtrack.data.MedCoachRepository
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import com.yeawon.s34598111.medtrack.data.OpenFdaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ViewModel fetch drug information from OpenFDA API
class MedCoachViewModel(application: Application): AndroidViewModel(application) {

    private val repository: MedCoachRepository = MedCoachRepository(application.applicationContext)
    private val medicationDao = MedTrackDatabase.getDatabase(application).medicationDao()

    private val _drugInfo = MutableStateFlow<OpenFdaResponse?>(null)

    val drugInfo: StateFlow<OpenFdaResponse?>
        get() = _drugInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean>
        get() = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?>
        get() = _errorMessage.asStateFlow()

    private val _medicationList = MutableStateFlow<List<String>>(emptyList())
    val medicationList: StateFlow<List<String>> = _medicationList.asStateFlow()

    // Loads all medication names for a specific patient
    fun loadPatientMedications(patientId: String) {
        viewModelScope.launch {
            val meds = medicationDao.getMedicationByPatient(patientId).first()
            _medicationList.value = meds.map { it.medicationName }
        }
    }

    // Search drug information
    fun searchDrug(drugName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = repository.getDrugInfo(drugName)
                _drugInfo.value = result
            }catch (e: Exception){
                _errorMessage.value = "Drug not found or network error"
                _drugInfo.value = null
            }
            _isLoading.value = false
        }
    }
}