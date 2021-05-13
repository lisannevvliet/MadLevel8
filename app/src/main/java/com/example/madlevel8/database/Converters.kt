package com.example.madlevel8.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class Converters {

    @TypeConverter
    fun fromList(value: ArrayList<Boolean>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toList(value: String): ArrayList<Boolean> {
        return Json.decodeFromString(value)
    }
}