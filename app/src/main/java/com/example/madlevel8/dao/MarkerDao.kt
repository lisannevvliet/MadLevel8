package com.example.madlevel8.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.madlevel8.model.Marker

@Dao
interface MarkerDao {

    @Query("SELECT * FROM Marker")
    suspend fun getAllMarkers(): List<Marker>

    @Insert
    suspend fun insertMarker(marker: Marker)

    /*@Delete
    suspend fun deleteMarker(marker: Marker)*/

    @Query("DELETE FROM Marker WHERE position = :position")
    suspend fun deleteMarker(position: String)

    @Query("DELETE FROM Marker")
    suspend fun deleteAllMarkers()
}