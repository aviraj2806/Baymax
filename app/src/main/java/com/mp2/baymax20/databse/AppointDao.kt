package com.mp2.baymax20.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppointDao {

    @Insert
    fun insertAppoint(appointEntity: AppointEntity)

    @Query("SELECT * FROM appoint WHERE appoint_by =:mobile")
    fun getAppointmentByPatient(mobile: String): List<AppointEntity>

    @Query("SELECT * FROM appoint WHERE appoint_to = :mobile")
    fun getAppointmentByDoctor(mobile: String): List<AppointEntity>

    @Query("UPDATE appoint SET appoint_status =:status WHERE timeStamp =:timeStamp")
    fun updateAppointmentStatus(status:String, timeStamp: String)

    @Query("SELECT * FROM appoint WHERE appoint_to =:mobile AND appoint_status =:status")
    fun getDoctorAppointmentByStatus(mobile:String,status:String) : List<AppointEntity>

    @Query("SELECT * FROM appoint WHERE appoint_by =:mobile AND appoint_status =:status")
    fun getPatientAppointmentByStatus(mobile:String,status:String) : List<AppointEntity>

}