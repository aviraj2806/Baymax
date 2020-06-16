package com.mp2.baymax20.databse

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class],version = 1)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}