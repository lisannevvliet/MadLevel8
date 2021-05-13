package com.example.madlevel8.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.madlevel8.dao.ChecklistDao
import com.example.madlevel8.model.Checklist

@Database(entities = [Checklist::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ChecklistRoomDatabase : RoomDatabase() {

    abstract fun checklistDao(): ChecklistDao

    companion object {
        private const val DATABASE_NAME = "CHECKLIST_DATABASE"

        @Volatile
        private var checklistRoomDatabaseInstance: ChecklistRoomDatabase? = null

        fun getDatabase(context: Context): ChecklistRoomDatabase? {
            if (checklistRoomDatabaseInstance == null) {
                synchronized(ChecklistRoomDatabase::class.java) {
                    if (checklistRoomDatabaseInstance == null) {
                        checklistRoomDatabaseInstance = Room.databaseBuilder(context.applicationContext, ChecklistRoomDatabase::class.java, DATABASE_NAME).build()
                    }
                }
            }
            return checklistRoomDatabaseInstance
        }
    }
}