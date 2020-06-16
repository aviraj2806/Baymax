package com.mp2.baymax20.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appoint")
data class AppointEntity (
    @PrimaryKey val timeStamp: String,
    @ColumnInfo (name = "appoint_by") val appoint_by: String,
    @ColumnInfo (name = "appoint_to") val appoint_to: String,
    @ColumnInfo (name = "appoint_status") val appoint_status: String,
    @ColumnInfo (name = "appoint_time") val appoint_time: String,
    @ColumnInfo (name = "hospital_name") val hospital_name: String
)