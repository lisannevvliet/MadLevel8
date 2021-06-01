package com.example.madlevel8.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.madlevel8.model.Marker

@Dao
interface MarkerDao {

    @Query("SELECT COUNT(*) FROM Marker WHERE address = :address")
    suspend fun existMarker(address: String): Int

    @Query("SELECT * FROM Marker")
    suspend fun getAllMarkers(): List<Marker>

    @Insert
    suspend fun insertMarker(marker: Marker)

    @Query("DELETE FROM Marker WHERE address = :address")
    suspend fun deleteMarker(address: String)

    @Query("DELETE FROM Marker")
    suspend fun deleteAllMarkers()
}