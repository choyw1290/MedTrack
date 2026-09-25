package com.yeawon.s34598111.medtrack.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yeawon.s34598111.medtrack.entity.TakenStatus

@Dao
interface TakenStatusDao {

    // Insert medication taken status
    @Insert
    suspend fun insert(status: TakenStatus)

    // Get all medication taken records for a patient on specific date
    @Query("SELECT * FROM takenStatus WHERE patientId = :patientId AND date = :date")
    suspend fun getTodayStatus(patientId: String, date: String): List<TakenStatus>

    // Remove old records that are not from today
    @Query("DELETE FROM takenstatus WHERE date != :today")
    suspend fun resetOldDays(today: String)
}