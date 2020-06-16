package com.mp2.baymax20.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "report")
data class ReportEntity (
    @PrimaryKey val timeStamp: String,
    @ColumnInfo(name = "report_link") val report_link:String,
    @ColumnInfo(name = "pres_link") val pres_link:String,
    @ColumnInfo(name = "report_by") val report_by:String,
    @ColumnInfo(name = "history") val history: String
)