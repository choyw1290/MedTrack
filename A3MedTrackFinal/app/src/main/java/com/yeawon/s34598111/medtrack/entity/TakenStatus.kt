package com.yeawon.s34598111.medtrack.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "takenStatus")
data class TakenStatus (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val medicationId:  Int,

    val patientId: String,

    val date: String,

    val isTaken: Boolean
)