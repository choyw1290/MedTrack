package com.yeawon.s34598111.medtrack.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medications",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["patientId"])]
)

data class Medication (
    @PrimaryKey(autoGenerate = true)
    val medicationId: Int = 0,

    val patientId: String,

    val medicationName: String,

    val dosage: String,

    val frequency: String,

    val time: String,

    val type: String,

    val notes: String
)