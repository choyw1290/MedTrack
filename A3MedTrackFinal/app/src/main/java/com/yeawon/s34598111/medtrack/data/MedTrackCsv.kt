package com.yeawon.s34598111.medtrack.data

import android.content.Context
import com.yeawon.s34598111.medtrack.entity.Medication
import com.yeawon.s34598111.medtrack.entity.Patient
import com.yeawon.s34598111.medtrack.entity.Symptom
import java.io.BufferedReader
import java.io.InputStreamReader

class MedTrackCsv {

    // Loads patient data from patients.csv
    fun loadPatients(context: Context): List<Patient> {
        val patients = mutableListOf<Patient>()

        try {
            val inputStream = context.assets.open("patients.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val values = line.split(",")

                    if(values.size >= 4){
                        patients.add(
                            Patient(
                                patientId = values[0].trim(),
                                phoneNumber = values[1].trim(),
                                name = values[2].trim(),
                                password = ""
                            )
                        )
                    }

                }
            }
        }catch (e: Exception){

        }
        return patients
    }

    // Loads medication data from medications.csv
    fun loadMedications(context: Context): List<Medication> {
        val medications = mutableListOf<Medication>()

        try {
            val inputStream = context.assets.open("medications.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val values = line.split(",")

                    if(values.size >= 7) {
                        medications.add(
                            Medication(
                                patientId = values[0].trim(),
                                medicationName = values[1].trim(),
                                dosage = values[2].trim(),
                                frequency = values[3].trim(),
                                time = values[4].trim(),
                                type = values[5].trim(),
                                notes = values[6].trim()
                            )
                        )
                    }

                }
            }
        }catch (e: Exception){

        }
        return medications
    }

    // Load symptom data from symptom.csv
    fun loadSymptoms(context: Context): List<Symptom> {
        val symptoms = mutableListOf<Symptom>()

        try {
            val inputStream = context.assets.open("symptoms.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val values = line.split(",")

                    if(values.size >= 5) {
                        symptoms.add(
                            Symptom(
                                patientId = values[0].trim(),
                                category = values[1].trim(),
                                severity = values[2].trim().toInt(),
                                notes = values[3].trim(),
                                dateTime = values[4].trim()
                            )
                        )
                    }
                }
            }
        }catch (e: Exception){

        }
        return symptoms
    }
}