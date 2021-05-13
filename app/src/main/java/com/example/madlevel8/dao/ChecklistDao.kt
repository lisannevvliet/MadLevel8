package com.example.madlevel8.dao

import androidx.room.*
import com.example.madlevel8.model.Checklist

@Dao
interface ChecklistDao {

    @Query("SELECT COUNT(*) FROM Checklist WHERE date = :date")
    suspend fun existChecklist(date: String): Int

    @Query("SELECT * FROM Checklist WHERE date = :date")
    suspend fun getChecklist(date: String): List<Checklist>

    @Insert
    suspend fun insertChecklist(checklist: Checklist)

    @Update
    suspend fun updateChecklist(checklist: Checklist)

    @Delete
    suspend fun deleteChecklist(checklist: Checklist)

    @Query("DELETE FROM Checklist")
    suspend fun deleteAllChecklists()
}