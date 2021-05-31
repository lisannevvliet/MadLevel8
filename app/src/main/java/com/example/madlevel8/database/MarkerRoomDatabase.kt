package com.example.madlevel8.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.madlevel8.dao.MarkerDao
import com.example.madlevel8.model.Marker

@Database(entities = [Marker::class], version = 1, exportSchema = false)
abstract class MarkerRoomDatabase : RoomDatabase() {

    abstract fun markerDao(): MarkerDao

    companion object {
        private const val DATABASE_NAME = "MARKER_DATABASE"

        @Volatile
        private var markerRoomDatabaseInstance: MarkerRoomDatabase? = null

        fun getDatabase(context: Context): MarkerRoomDatabase? {
            if (markerRoomDatabaseInstance == null) {
                synchronized(MarkerRoomDatabase::class.java) {
                    if (markerRoomDatabaseInstance == null) {
                        markerRoomDatabaseInstance = Room.databaseBuilder(context.applicationContext, MarkerRoomDatabase::class.java, DATABASE_NAME).build()
                    }
                }
            }
            return markerRoomDatabaseInstance
        }
    }
}