package com.yeawon.s34598111.medtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.yeawon.s34598111.medtrack.BuildConfig
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import com.yeawon.s34598111.medtrack.data.MedTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


// ViewModel for handling retrieval of patient statistics from database
// and use gemini to generate insights based on aggregated data
class ClinicianViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MedTrackRepository =
        MedTrackRepository(
            application.applicationContext,
            MedTrackDatabase.getDatabase(application).patientDao(),
            MedTrackDatabase.getDatabase(application).medicationDao(),
            MedTrackDatabase.getDatabase(application).symptomDao()
        )
    // Gemini Ai model for generating clinical insights
    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.apiKey
    )

    private val _totalPatients = MutableStateFlow(0)
    val totalPatients: StateFlow<Int> = _totalPatients

    private val _avgMedications = MutableStateFlow(0.0)
    val avgMedications: StateFlow<Double> = _avgMedications

    private val _commonSymptom = MutableStateFlow("")
    val commonSymptom: StateFlow<String> = _commonSymptom

    private val _avgSeverity = MutableStateFlow(0.0)
    val avgSeverity: StateFlow<Double> = _avgSeverity

    private val _insights = MutableStateFlow("")
    val insights: StateFlow<String> = _insights

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Loads and calculate aggregate statistics from database
    fun loadStats() {
        viewModelScope.launch {

            val patients = repository.getAllPatientsList()

            _totalPatients.value = patients.size

            var totalMeds = 0

            // Calculate total medications
            for (p in patients) {
                totalMeds += repository.getMedicationCount(p.patientId)
            }

            // Calculate average medications
            _avgMedications.value =
                if (patients.isNotEmpty()) totalMeds.toDouble() / patients.size
                else 0.0

            // Retrieve most common symptom
            _commonSymptom.value = repository.getMostCommonSymptom() ?: "N/A"

            // Get average symptom severity
            _avgSeverity.value = repository.getAverageSeverity() ?: 0.0
        }
    }

    // Use Gemini Ai to analyze clinician data and generate clinical pattern
    fun findPatterns() {
        viewModelScope.launch {

            _isLoading.value = true

            try {
                val dataSummary =
                    "Total Patients:" + _totalPatients.value + "\n" +
                            "Average Medications:" + _avgMedications.value + "\n" +
                            "Most Common Symptom:" + _commonSymptom.value + "\n" +
                            "Average Severity:" + _avgSeverity.value

                val prompt =
                    "You are a clinical data analyst.\n" +
                            "Analyze this dataset:\n" +
                            dataSummary +
                            "\n\nReturn 3 clear patterns about medication, symptoms, or severity."

                val response = generativeModel.generateContent(content { text(prompt) })

                _insights.value = response.text ?: "No insights generated"
            } catch (e: Exception){
                _insights.value = "Error: ${e.message}"
            }

            _isLoading.value = false
        }
    }
}