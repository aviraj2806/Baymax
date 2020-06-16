package com.mp2.baymax20.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HospitalDao {

    @Insert
    fun insertHospital(hospitalEntity: HospitalEntity)

    @Query("SELECT * FROM hospital")
    fun getAllHospital(): List<HospitalEntity>

    @Query("SELECT * FROM hospital WHERE name =:name")
    fun getHospitalByName(name: String): HospitalEntity
}