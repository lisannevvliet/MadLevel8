package com.example.madlevel8.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.madlevel8.R
import com.example.madlevel8.databinding.FragmentChecklistBinding
import com.example.madlevel8.model.Checklist
import com.example.madlevel8.vm.ChecklistViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChecklistFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChecklistViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val checkboxes = ArrayList<CheckBox>()

    // Make sure the language of the date button matches the system language.
    val locale =
        if (Locale.getDefault().displayLanguage == "English") {
            Locale.US
        } else {
            Locale("nl")
        }

    // Retrieve today's date.
    var date = SimpleDateFormat("d MMMM yyyy", locale).format(calendar.time)

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

        // Enable a different AppBar for this fragment.
        setHasOptionsMenu(true)

        // Add all checkboxes to an ArrayList, to be able to loop over them later.
        checkboxes.addAll(listOf(
                binding.cbFruit1, binding.cbFruit2, binding.cbFruit3,
                binding.cbVegetables1, binding.cbVegetables2, binding.cbVegetables3,
                binding.cbWholeGrains1, binding.cbWholeGrains2, binding.cbWholeGrains3,
                binding.cbLegumes1, binding.cbLegumes2,
                binding.cbPlantbasedDairy1,
                binding.cbNutsSeedsPeanutsAvocado1, binding.cbNutsSeedsPeanutsAvocado2,
                binding.cbFlaxSeedsChiaSeeds1,
                binding.cbSupplements1, binding.cbSupplements2, binding.cbSupplements3,
                binding.cbDrinks1, binding.cbDrinks2, binding.cbDrinks3, binding.cbDrinks4, binding.cbDrinks5, binding.cbDrinks6
        ))

        // Create an OnDateSetListener.
        val listener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Retrieve the selected date from the calendar.
            date = SimpleDateFormat("d MMMM yyyy", locale).format(calendar.time)

            // Set the text of the date button to the selected date.
            binding.btnDate.text = date

            // Retrieve the checklist of the selected date and select the correct checkboxes.
            viewModel.getChecklist(date, checkboxes)
        }

        // Upon a click on the date button, show the DatePickerDialog and point to today's date.
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

        // Set the text of the date button to today's date.
        binding.btnDate.text = date

        // Retrieve the checklist of today and select the correct checkboxes.
        viewModel.getChecklist(date, checkboxes)
    }

    // Inflate the custom AppBar.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    // Delete all checklists upon a click on the AppBar trash can, with an option to undo the action.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                // Save the checklist of the selected date before opening the AlertDialog.
                updateChecklist()

                AlertDialog.Builder(context)
                    .setTitle(R.string.confirmation)
                    .setMessage(getString(R.string.action, "products"))
                    .setPositiveButton(R.string.yes) { _, _ ->
                        // Delete all checklists in the database, with an option to undo the action.
                        viewModel.deleteAllChecklists(date, checkboxes, binding.btnDate)
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                        // Show a Snackbar message which says that the action has been cancelled.
                        Snackbar.make(binding.btnDate, R.string.cancelled, Snackbar.LENGTH_LONG).show()
                    }
                    .create()
                    .show()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    // Add, update or remove the checklist of the corresponding date.
    private fun updateChecklist() {
        val selected = ArrayList<Boolean>()

        // Check for each checkbox whether it is selected and add this to the list.
        for (checkbox in checkboxes) {
            selected.add(checkbox.isChecked)
        }

        viewModel.updateChecklist(Checklist(date, selected))
    }

    // Save the checklist of the selected date before leaving the fragment.
    override fun onPause() {
        super.onPause()

        updateChecklist()
    }

    // Release the view if the fragment is destroyed to prevent a memory leak.
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}