package com.mp2.baymax20.databse

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ReportEntity::class],version = 1)
abstract class ReportDatabase: RoomDatabase() {

    abstract fun reportDao():ReportDao

}