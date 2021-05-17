package com.example.madlevel8.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.madlevel8.databinding.FragmentAddProductBinding
import com.example.madlevel8.model.Product
import com.example.madlevel8.vm.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Allow changes to this fragment's AppBar.
        setHasOptionsMenu(true)

        // Retrieve the fragment result from the HomeFragment and fill the product name or barcode with it.
        setFragmentResultListener(requestKey) { _, bundle ->
            val result = bundle.getString(bundleKey)

            if (result != null) {
                // If the result only consists of digits, fill the barcode with the result.
                if (result.all { it.isDigit() }) {
                    binding.etBarcode.setText(result)
                // If the result also consists of letters, fill the product name with the result.
                } else {
                    binding.etName.setText(result)
                }
            }
        }

        // Show the barcode scanner upon a click on the scan button.
        binding.btnScan.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)

            // Remove the default text.
            integrator.setPrompt("")
            // Disable the beep upon a successful scan.
            integrator.setBeepEnabled(false)
            // Start the barcode scanner.
            integrator.initiateScan()
        }

        // Add the product to the database upon a click on add button.
        binding.btnAdd.setOnClickListener {
            val name = binding.etName.text.toString()
            val barcode = binding.etBarcode.text.toString()
            val vegan = binding.sVegan.isChecked

            // Check if the product name is filled in.
            if (TextUtils.isEmpty(name)) {
                // Show a Snackbar message which says that the product name needs to be filled in.
                Snackbar.make(binding.btnAdd, "Please fill in the product name.", Snackbar.LENGTH_LONG).show()
            } else {
                // Check if the barcode is filled in and create a product object with the filled in fields.
                val product =
                        if (TextUtils.isEmpty(barcode)) {
                            Product(name, vegan)
                        } else {
                            Product(name, vegan, barcode.toLong())
                }

                // Add the product to the database.
                viewModel.insertProduct(product)

                // Show a Snackbar message which says that the product was successfully added.
                Snackbar.make(binding.btnAdd, "${product.name} was successfully added.", Snackbar.LENGTH_LONG).show()

                // Navigate back to the HomeFragment.
                findNavController().popBackStack()
            }
        }
    }

    // Navigate back to the HomeFragment upon a click on the AppBar's back button.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Retrieve the result of the barcode scanner.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result.contents != null) {
            val barcode = result.contents

            // Fill the barcode with the result.
            binding.etBarcode.setText(barcode)
        }
    }

    // Release the view if the fragment is destroyed to prevent a memory leak.
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}