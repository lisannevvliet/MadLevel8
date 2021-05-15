package com.example.madlevel8.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.madlevel8.R
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

        // Enable a different AppBar for this fragment.
        setHasOptionsMenu(true)

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

        binding.btnAdd.setOnClickListener {
            val name = binding.etName.text.toString()
            val vegan = binding.sVegan.isChecked
            val barcode = binding.etBarcode.text.toString()

            if (TextUtils.isEmpty(name)) {
                Snackbar.make(binding.btnAdd, "Please fill in a valid name.", Snackbar.LENGTH_LONG).show()
            } else {
                if (TextUtils.isEmpty(barcode)) {
                    val product = Product(name, vegan)
                    viewModel.insertProduct(product)
                    Snackbar.make(binding.btnAdd, "${product.name} was successfully added.", Snackbar.LENGTH_LONG).show()
                    findNavController().popBackStack()
                } else {
                    val product = Product(name, vegan, barcode.toLong())
                    viewModel.insertProduct(product)
                    Snackbar.make(binding.btnAdd, "${product.name} was successfully added.", Snackbar.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
            }
        }

        // Retrieve the fragment result from the HomeFragment and pass it onto the bind function.
        setFragmentResultListener(requestKey) { _, bundle ->
            val result = bundle.getString(bundleKey)

            if (result != null) {
                // If the result only consists of digits, fill the barcode with the result.
                if (result.all { it.isDigit() } ) {
                    binding.etBarcode.setText(result)
                // If the result also consists of letters, fill the product name with the result.
                } else {
                    binding.etName.setText(result)
                }
            }
        }
    }

    // .....................................................................
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result.contents != null) {

            val barcode = result.contents.toLong().toString()

            binding.etBarcode.setText(barcode)
        }
    }
}