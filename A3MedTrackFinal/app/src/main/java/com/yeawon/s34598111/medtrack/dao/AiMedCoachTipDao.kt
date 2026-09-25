package com.yeawon.s34598111.medtrack.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yeawon.s34598111.medtrack.entity.AiMedCoachTip

@Dao
interface AiMedCoachTipDao {

    // Insert Ai generated medication tip
    @Insert
    suspend fun insert(tip: AiMedCoachTip)

    // Retrieve all Ai tips for a specific patient, sorted by newest first
    @Query("SELECT * FROM medCoachTips WHERE patientId = :patientId ORDER BY id DESC")
    suspend fun getTipsByPatient(patientId: String): List<AiMedCoachTip>
}