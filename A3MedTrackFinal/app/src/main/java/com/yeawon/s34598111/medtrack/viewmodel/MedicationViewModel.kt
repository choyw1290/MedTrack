package com.yeawon.s34598111.medtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import com.yeawon.s34598111.medtrack.data.MedTrackRepository
import com.yeawon.s34598111.medtrack.entity.Medication
import com.yeawon.s34598111.medtrack.entity.TakenStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

// ViewModel for managing medication data and track taken status
class MedicationViewModel(application: Application): AndroidViewModel(application) {
    private val repository: MedTrackRepository

    val allMedications: StateFlow<List<Medication>>

    private val takenDao = MedTrackDatabase.getDatabase(application).takenStatusDao()

    private val _takenMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val takenMap: StateFlow<Map<Int, Boolean>> = _takenMap

    init{
        val database = MedTrackDatabase.Companion.getDatabase(application)
        repository = MedTrackRepository(
            application.applicationContext,
            database.patientDao(),
            database.medicationDao(),
            database.symptomDao()
        )
        allMedications = repository.getAllMedications()
            .stateIn(
                viewModelScope,
                SharingStarted.Companion.Lazily,
                emptyList()
            )
    }

    // Retrieve medications for specific patient
    fun getMedicationByPatient(patientId: String): Flow<List<Medication>> {
        return repository.getMedicationByPatient(patientId)
    }

    // Insert new medication into database
    fun insertMedication(medication: Medication) {
        viewModelScope.launch {
            repository.insertMedication(medication)
        }
    }

    // Get today's date
    private fun getTodayDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date())
    }

    // Loads today's taken status for a patient and map
    fun loadTakenStatus(patientId: String) {
        viewModelScope.launch {
            val today = getTodayDate()

            val list = takenDao.getTodayStatus(patientId, today)

            val map = mutableMapOf<Int, Boolean>()

            for (item in list) {
                map[item.medicationId] = item.isTaken
            }
            _takenMap.value = map
        }
    }

    // Updates medication taken status
    fun toggleTaken(medId: Int, patientId: String, isTaken: Boolean) {
        viewModelScope.launch {

            val today = getTodayDate()

            val data = TakenStatus(
                medicationId = medId,
                patientId = patientId,
                date = today,
                isTaken = isTaken
            )

            takenDao.insert(data)

            val currentMap = _takenMap.value.toMutableMap()
            currentMap[medId] = isTaken
            _takenMap.value = currentMap
        }
    }
}