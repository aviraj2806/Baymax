package com.mp2.baymax20.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity (
    @PrimaryKey val mobile: String,
    @ColumnInfo (name = "type") val type: String,
    @ColumnInfo (name = "name") val name: String,
    @ColumnInfo (name = "email") val email: String,
    @ColumnInfo (name = "image") val image: String,
    @ColumnInfo (name = "pass") val pass: String,
    @ColumnInfo (name = "doctor_spe") val spe: String,
    @ColumnInfo (name = "doctor_hospital") val hospital: String,
    @ColumnInfo (name = "patient_sos") val sos: String
)