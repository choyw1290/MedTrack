package com.yeawon.s34598111.medtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.yeawon.s34598111.medtrack.BuildConfig
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


// ViewModel generate Ai diet recommendations by using medication data from patient
// Generate simple diet plan including foods to eat, avoid and daily suggestion
class DietGenAiViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = MedTrackDatabase.getDatabase(application).medicationDao()

    private val _dietPlan = MutableStateFlow("")
    val dietPlan: StateFlow<String> = _dietPlan

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.apiKey
    )

    // Generates a diet plan based on patient's information
    fun generateDiet(patientId: String){
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val meds = dao.getMedicationByPatient(patientId).first()

                val medContext = if(meds.isEmpty()) {
                    "No medications recorded"
                } else {
                    meds.joinToString("\n") {
                        "-${it.medicationName} (${it.dosage}, ${it.frequency}"
                    }
                }

                val prompt =
                    "You are a medical diet assistant.\n" +
                            "Based on these patient medications:\n" +
                            medContext +
                            "\n\nProvide:\n" +
                            "1. Foods to eat\n" +
                            "2. Foods to avoid\n" +
                            "3. Simple daily diet plan\n" +
                            "Keep it short and easy to understand."

                val response = generativeModel.generateContent(content { text(prompt) })

                _dietPlan.value = response.text ?: "No response"
            } catch (e: Exception) {
                _dietPlan.value = "Error: ${e.message}"
            }

            _isLoading.value = false
        }
    }

}