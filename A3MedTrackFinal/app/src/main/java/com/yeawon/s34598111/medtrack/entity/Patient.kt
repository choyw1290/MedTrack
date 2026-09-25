package com.yeawon.s34598111.medtrack.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient (
    @PrimaryKey
    val patientId: String,

    val phoneNumber: String,

    val name: String,

    val password: String
)