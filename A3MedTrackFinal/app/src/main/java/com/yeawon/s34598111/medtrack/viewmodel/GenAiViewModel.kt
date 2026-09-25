package com.yeawon.s34598111.medtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yeawon.s34598111.medtrack.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.yeawon.s34598111.medtrack.data.MedTrackDatabase
import com.yeawon.s34598111.medtrack.entity.AiMedCoachTip
import com.yeawon.s34598111.medtrack.entity.Medication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ViewModel generate medication reminder tips using gemini based on patient medications
// and stores generated tips in local database
class GenAiViewModel(application: Application) : AndroidViewModel(application) {


    private val dao = MedTrackDatabase.getDatabase(application).medCoachTipDao()

    private val _tip = MutableStateFlow("")
    val tip: StateFlow<String> = _tip

    private val _history = MutableStateFlow<List<AiMedCoachTip>>(emptyList())
    val history: StateFlow<List<AiMedCoachTip>> = _history

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.apiKey
    )

    // Generate a medication reminder tip using Ai based on patient medication data
    fun generateTip(patientId: String) {
        viewModelScope.launch {

            _isLoading.value = true

            try {

                val medications = MedTrackDatabase.getDatabase(getApplication()).medicationDao().getMedicationByPatient(patientId).first()

                var medContext = ""

                for(med in medications) {
                    medContext += "- ${med.medicationName}, ${med.dosage}, ${med.frequency}, ${med.time}\n"
                }

                val prompt =
                    "Patient medication details:\n" + medContext +
                            "\nPatient may need help remembering these medications.\n" +
                            "Give a short, friendly reminder message that mentions the medications."

                val response = generativeModel.generateContent(
                    content{
                        text(prompt)
                    }
                )
                val result = response.text ?: "No response"

                _tip.value = result

                dao.insert(AiMedCoachTip(
                    patientId = patientId,
                    tip = result
                ))

                _history.value = dao.getTipsByPatient(patientId)

            } catch (e: Exception) {
                _tip.value = "Failed to generate tip"
            }
            _isLoading.value = false
        }
    }

    // Loads previous generated Ai tips for patient
    fun loadHistory(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _history.value = dao.getTipsByPatient(patientId)
        }
    }
}