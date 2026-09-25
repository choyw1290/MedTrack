package com.yeawon.s34598111.medtrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yeawon.s34598111.medtrack.dao.AiMedCoachTipDao
import com.yeawon.s34598111.medtrack.dao.MedicationDao
import com.yeawon.s34598111.medtrack.dao.PatientDao
import com.yeawon.s34598111.medtrack.dao.SymptomDao
import com.yeawon.s34598111.medtrack.dao.TakenStatusDao
import com.yeawon.s34598111.medtrack.entity.AiMedCoachTip
import com.yeawon.s34598111.medtrack.entity.Medication
import com.yeawon.s34598111.medtrack.entity.Patient
import com.yeawon.s34598111.medtrack.entity.Symptom
import com.yeawon.s34598111.medtrack.entity.TakenStatus

@Database(entities = [Patient::class, Medication::class, Symptom::class, AiMedCoachTip::class, TakenStatus::class], version = 3)
abstract class MedTrackDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun medicationDao(): MedicationDao
    abstract fun symptomDao(): SymptomDao
    abstract fun medCoachTipDao(): AiMedCoachTipDao

    abstract fun takenStatusDao(): TakenStatusDao

    companion object{
        @Volatile
        private var INSTANCE: MedTrackDatabase? = null

        fun getDatabase(context: Context): MedTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedTrackDatabase::class.java,
                    "medtrack_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}