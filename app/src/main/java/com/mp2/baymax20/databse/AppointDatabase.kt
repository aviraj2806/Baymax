package com.mp2.baymax20.databse

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppointEntity::class], version = 1)
abstract class AppointDatabase: RoomDatabase() {

    abstract fun appointDao(): AppointDao
}