package com.yeawon.s34598111.medtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import com.yeawon.s34598111.medtrack.data.MedTrackRepository
import com.yeawon.s34598111.medtrack.entity.Symptom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SymptomViewModel(application: Application): AndroidViewModel(application) {

    private val repository: MedTrackRepository

    val allSymptoms: StateFlow<List<Symptom>>

    init{
        val database = MedTrackDatabase.Companion.getDatabase(application)
        repository = MedTrackRepository(
            application.applicationContext,
            database.patientDao(),
            database.medicationDao(),
            database.symptomDao()
        )
        allSymptoms = repository.getAllSymptoms()
            .stateIn(
                viewModelScope,
                SharingStarted.Companion.Lazily,
                emptyList()
            )
    }

    // Retrieve symptoms for specific patient
    fun getSymptomByPatient(patientId: String): Flow<List<Symptom>> {
        return repository.getSymptomByPatient(patientId)
            .stateIn(
                viewModelScope,
                SharingStarted.Companion.Lazily,
                emptyList()
            )
    }

    fun insertSymptom(symptom: Symptom) {
        viewModelScope.launch {
            repository.insertSymptom(symptom)
        }
    }
}