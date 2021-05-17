package com.example.madlevel8.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madlevel8.R
import com.example.madlevel8.databinding.FragmentHomeBinding
import com.example.madlevel8.vm.ProductViewModel
import com.google.zxing.integration.android.IntentIntegrator

// Initialize the bundle and request key for the fragment result.
const val bundleKey = ""
const val requestKey = ""

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show the search results upon a change in the search query.
        binding.svProducts.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // Uncheck the vegan chip.
                binding.cVegan.isChecked = false

                // Retrieve all products which match the search query or barcode.
                viewModel.getProducts(query, binding.tvNoProductsFound)

                // Set the fragment result to the search query or barcode.
                setFragmentResult(requestKey, bundleOf(bundleKey to query))

                return false
            }
        })

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

        // Update the search results according to whether the vegan chip is selected.
        binding.cVegan.setOnCheckedChangeListener { _, checked ->
            // Retrieve the search query.
            val query = binding.svProducts.query.toString()

            // Retrieve all (vegan) products which match the search query or barcode.
            viewModel.getProducts(query, binding.tvNoProductsFound, checked)
        }

        // Hide the no products found error by default.
        binding.tvNoProductsFound.visibility =  View.INVISIBLE

        // Initialize the recycler view with a linear layout manager, adapter.
        binding.rvProducts.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvProducts.adapter = viewModel.productAdapter

        // Attach the ItemTouchHelper to recognize left swipes within the RecyclerView.
        itemTouchHelper().attachToRecyclerView(binding.rvProducts)

        // Fill the RecyclerView with all products, sorted by the date they were added.
        viewModel.getProducts("", binding.tvNoProductsFound)

        // Navigate to the AddProductFragment upon a click on the floating action button.
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addProductFragment)
        }
    }

    // Retrieve the result of the barcode scanner.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result.contents != null) {
            val barcode = result.contents

            // Set the barcode as the search query to retrieve all products which match the barcode.
            binding.svProducts.setQuery(barcode, false)
        }
    }

    // Enable touch behavior (like swipe and move) on each ViewHolder, and use callbacks to signal when a user is performing these actions.
    private fun itemTouchHelper(): ItemTouchHelper {
        // Create the ItemTouch helper, only enable left swipe.
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            // Disable the ability to move items up and down.
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            // Delete the product from the database upon a swipe to the left, with an option to undo the action.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteProduct(viewHolder.adapterPosition, binding.rvProducts)
            }
        }
        return ItemTouchHelper(callback)
    }

    // Release the view if the fragment is destroyed to prevent a memory leak.
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}