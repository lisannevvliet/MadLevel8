package com.example.madlevel8.vm

import android.app.Application
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import com.example.madlevel8.model.Checklist
import com.example.madlevel8.repository.ChecklistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {

    private val checklistRepository = ChecklistRepository(application.applicationContext)

    // Retrieve the checklist of the specified date and select the correct checkboxes.
    fun getChecklist(date: String, checkboxes: ArrayList<CheckBox>) {
        CoroutineScope(Dispatchers.Main).launch {
            val checklist = withContext(Dispatchers.IO) { checklistRepository.getChecklist(date) }

            // If there is already an entry of the specified date in the database, select the saved checkboxes.
            if (checklist.isNotEmpty()) {
                for ((counter, checkbox) in checkboxes.withIndex()) {
                    checkbox.isChecked = checklist[0].selected[counter]
                }
            // If there is not already an entry of the specified date in the database, deselect all checkboxes.
            } else {
                for (checkbox in checkboxes) {
                    checkbox.isChecked = false
                }
            }
        }
    }

    // Add, update or remove the selected checkboxes with the corresponding date.
    fun updateChecklist(checklist: Checklist) {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if there is already an entry of the specified date in the database.
            val exists = checklistRepository.existChecklist(checklist.date) != 0

            // Check if at least one of the checkboxes is selected.
            var empty = true
            for (checkbox in checklist.selected) {
                if (checkbox) {
                    empty = false
                }
            }

            // If there is already an entry in the database but all checkboxes are deselected, delete the entry.
            if (exists && empty) {
                checklistRepository.deleteChecklist(checklist)
            // If there is not an entry in the database yet and at least one of the checkboxes is selected, create an entry.
            } else if (!exists && !empty) {
                checklistRepository.insertChecklist(checklist)
            // If there is already an entry in the database but the selected checkboxes changed, update the entry.
            } else {
                checklistRepository.updateChecklist(checklist)
            }
        }
    }

    // Delete all checklists in the database.
    fun deleteAllChecklists() {
        CoroutineScope(Dispatchers.IO).launch { checklistRepository.deleteAllChecklists() }
    }
}