package com.yeawon.s34598111.medtrack.data

import android.content.Context

// Seed the database with initial data from CSV files
class DatabaseSeeder(private val repository: MedTrackRepository) {

    // Loads patient, medication and symptom data from CSV files
    // and insert them into the database
    suspend fun seedDatabase(context: Context) {

        val prefs = context.getSharedPreferences("medtrack_prefs", Context.MODE_PRIVATE)

        // Skip seeding if already done
        if (prefs.getBoolean("database_seeded", false)) return

        val csv = MedTrackCsv()

        val patients = csv.loadPatients(context)
        patients.forEach {
            repository.insertPatient(it) }

        val medications = csv.loadMedications(context)
        medications.forEach { repository.insertMedication(it) }

        val symptoms = csv.loadSymptoms(context)
        symptoms.forEach { repository.insertSymptom(it) }

        // Mark database as seeded
        prefs.edit().putBoolean("database_seeded", true).apply()
    }
}