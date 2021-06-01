package com.example.madlevel8.repository

import android.content.Context
import com.example.madlevel8.dao.MarkerDao
import com.example.madlevel8.database.MarkerRoomDatabase
import com.example.madlevel8.model.Marker

class MarkerRepository(context: Context) {

    private var markerDao: MarkerDao

    init {
        val markerRoomDatabase = MarkerRoomDatabase.getDatabase(context)
        markerDao = markerRoomDatabase!!.markerDao()
    }

    suspend fun existMarker(position: String) = markerDao.existMarker(position)

    suspend fun getAllMarkers() = markerDao.getAllMarkers()

    suspend fun insertMarker(marker: Marker) = markerDao.insertMarker(marker)

    suspend fun deleteMarker(address: String) = markerDao.deleteMarker(address)

    suspend fun deleteAllMarkers() = markerDao.deleteAllMarkers()
}