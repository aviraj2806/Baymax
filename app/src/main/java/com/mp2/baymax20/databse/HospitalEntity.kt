package com.mp2.baymax20.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hospital")
data class HospitalEntity (

    @PrimaryKey val name:String,
    @ColumnInfo(name = "image") val image:String,
    @ColumnInfo(name = "loc") val loc: String,
    @ColumnInfo(name = "directions") val directions: String,
    @ColumnInfo(name = "rating") val rating: String,
    @ColumnInfo(name = "contact") val contact: String

)