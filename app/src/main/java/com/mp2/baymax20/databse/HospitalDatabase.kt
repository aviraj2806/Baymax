package com.mp2.baymax20.databse

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HospitalEntity::class],version = 1)
abstract class HospitalDatabase: RoomDatabase() {
    abstract fun hospitalDao(): HospitalDao
}