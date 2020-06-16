package com.mp2.baymax20.databse

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReportDao {

    @Insert
    fun insertReport(reportEntity: ReportEntity)

    @Query("SELECT * FROM report")
    fun getAllReport(): List<ReportEntity>

    @Query("SELECT * FROM report WHERE report_by =:mobile")
    fun getAllReportByUser(mobile: String): List<ReportEntity>

}