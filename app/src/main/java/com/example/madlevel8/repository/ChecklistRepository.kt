package com.example.madlevel8.repository

import android.content.Context
import com.example.madlevel8.dao.ChecklistDao
import com.example.madlevel8.database.ChecklistRoomDatabase
import com.example.madlevel8.model.Checklist

class ChecklistRepository(context: Context) {

    private var checklistDao: ChecklistDao

    init {
        val checklistRoomDatabase = ChecklistRoomDatabase.getDatabase(context)
        checklistDao = checklistRoomDatabase!!.checklistDao()
    }

    suspend fun existChecklist(date: String): Int = checklistDao.existChecklist(date)

    suspend fun getChecklist(date: String): List<Checklist> = checklistDao.getChecklist(date)

    suspend fun insertChecklist(checklist: Checklist) = checklistDao.insertChecklist(checklist)

    suspend fun updateChecklist(checklist: Checklist) = checklistDao.updateChecklist(checklist)

    suspend fun deleteChecklist(checklist: Checklist) = checklistDao.deleteChecklist(checklist)

    suspend fun deleteAllChecklists() = checklistDao.deleteAllChecklists()
}