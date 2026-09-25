package com.yeawon.s34598111.medtrack.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medCoachTips")
data class AiMedCoachTip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val patientId: String,

    val tip: String
)