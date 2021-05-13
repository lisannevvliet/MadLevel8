package com.example.madlevel8.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.madlevel8.databinding.FragmentChecklistBinding
import com.example.madlevel8.model.Checklist
import com.example.madlevel8.vm.ChecklistViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChecklistFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChecklistViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val checkboxes = ArrayList<CheckBox>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment.
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add all checkboxes to an ArrayList, to be able to loop over them later.
        checkboxes.addAll(
            listOf(
                binding.cbFruit1, binding.cbFruit2, binding.cbFruit3,
                binding.cbVegetables1, binding.cbVegetables2, binding.cbVegetables3,
                binding.cbWholeGrains1, binding.cbWholeGrains2, binding.cbWholeGrains3,
                binding.cbLegumes1, binding.cbLegumes2,
                binding.cbPlantbasedDairy1,
                binding.cbNutsSeedsPeanutsAvocado1, binding.cbNutsSeedsPeanutsAvocado2,
                binding.cbFlaxSeedsChiaSeeds1,
                binding.cbSupplements1, binding.cbSupplements2, binding.cbSupplements3,
                binding.cbDrinks1, binding.cbDrinks2, binding.cbDrinks3, binding.cbDrinks4, binding.cbDrinks5, binding.cbDrinks6
            )
        )

        // Set the text of the button to today's date.
        val today = SimpleDateFormat("d MMMM yyyy", Locale.US).format(calendar.time)
        binding.btnDate.text = today

        // Retrieve the checklist of today and select the correct checkboxes.
        viewModel.getChecklist(today, checkboxes)

        // Create an OnDateSetListener.
        val listener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Set the text of the button to the selected date.
            val date = SimpleDateFormat("d MMMM yyyy", Locale.US).format(calendar.time)
            binding.btnDate.text = date

            // Retrieve the checklist of the selected date and select the correct checkboxes.
            viewModel.getChecklist(date, checkboxes)
        }

        // Upon a click on the button, show the DatePickerDialog and point to today's date.
        binding.btnDate.setOnClickListener {
            activity?.let { it ->
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(it, listener, year, month, day).show()
            }

            // Save the checklist of the previously selected date.
            updateChecklist()
        }

        // Clear the database and deselect all checkboxes. Only for testing purposes, will be deleted later.
        binding.btnClear.setOnClickListener {
            viewModel.deleteAllChecklists()

            for (checkbox in checkboxes) {
                checkbox.isChecked = false
            }
        }
    }

    // Save the checklist of the selected date before leaving the fragment.
    override fun onPause() {
        super.onPause()

        updateChecklist()
    }

    // Add, update or remove the selected checkboxes with the corresponding date.
    private fun updateChecklist() {
        val selected = ArrayList<Boolean>()

        // Check for each checkbox whether it is selected and add this to the list.
        for (checkbox in checkboxes) {
            selected.add(checkbox.isChecked)
        }

        // Save the date that is displayed in the button.
        val date = binding.btnDate.text as String

        viewModel.updateChecklist(Checklist(date, selected))
    }
}