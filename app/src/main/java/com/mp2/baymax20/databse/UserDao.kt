package com.mp2.baymax20.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insertUser(userEntity: UserEntity)

    @Query("SELECT * FROM user WHERE mobile =:mobile")
    fun getUserByMobile(mobile: String): UserEntity

    @Query("UPDATE user SET doctor_spe =:specialization WHERE mobile =:mobile")
    fun updateDoctorSpe(specialization: String,mobile: String)

    @Query("UPDATE user SET doctor_hospital =:hospital WHERE mobile =:mobile")
    fun updateDoctorHospital(hospital: String,mobile: String)

    @Query("UPDATE user SET patient_sos =:sos WHERE mobile =:mobile")
    fun updatePatientSos(sos: String,mobile: String)

    @Query("SELECT * FROM user WHERE doctor_spe =:spe AND doctor_hospital =:hospital")
    fun getDoctorByHospitalAndSpe(spe: String,hospital: String): List<UserEntity>
}