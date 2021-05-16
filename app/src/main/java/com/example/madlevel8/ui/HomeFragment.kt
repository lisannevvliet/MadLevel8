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
import com.example.madlevel8.adapter.ProductAdapter
import com.example.madlevel8.databinding.FragmentHomeBinding
import com.example.madlevel8.model.Product
import com.example.madlevel8.vm.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator

// Initialize the bundle and request key for the fragment result.
const val bundleKey = ""
const val requestKey = ""

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()

    private val products = arrayListOf<Product>()
    private val backupProducts = arrayListOf<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the no products found error by default.
        binding.tvNoProductsFound.visibility =  View.INVISIBLE

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

        // Navigate to the AddProductFragment upon a click on the floating action button.
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addProductFragment)
        }

        // Initialize the recycler view with a linear layout manager, adapter.
        binding.rvProducts.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvProducts.adapter = ProductAdapter(products)

        // Attach the ItemTouchHelper to recognize left swipes within the RecyclerView.
        itemTouchHelper().attachToRecyclerView(binding.rvProducts)

        // Observe the LiveData with the search results and update the RecyclerView when it changes.
        viewModel.products.observe(viewLifecycleOwner, {
            products.clear()
            products.addAll(it)
            ProductAdapter(products).notifyDataSetChanged()

            if (products.isNotEmpty()) {
                binding.tvNoProductsFound.visibility = View.INVISIBLE
            } else {
                binding.tvNoProductsFound.visibility = View.VISIBLE
            }
        })

        // Show the search results upon a submitted search query.
        binding.svProducts.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Retrieve all products which match the search query and update the LiveData.
                viewModel.getProducts(query)

                // Set the fragment result to the search query.
                setFragmentResult(requestKey, bundleOf(bundleKey to query))

                return false
            }

            // Clear the search results when the search query is changed.
            override fun onQueryTextChange(newText: String): Boolean {
                // Hide the no products found errors again.
                binding.tvNoProductsFound.visibility = View.INVISIBLE

                // Uncheck the vegan chip.
                binding.cVegan.isChecked = false

                // Clear the RecyclerView.
                products.clear()
                ProductAdapter(products).notifyDataSetChanged()

                return false
            }
        })

        // Update the search results according to whether the vegan chip is selected.
        binding.cVegan.setOnCheckedChangeListener { _, checked ->
            // Retrieve the search query.
            val query = binding.svProducts.query

            if (query.isNotBlank()) {
                if (checked) {
                    // Retrieve all vegan products which match the search query and update the LiveData.
                    viewModel.getVeganProducts(query.toString())
                } else {
                    // Retrieve all products which match the search query and update the LiveData.
                    viewModel.getProducts(query.toString())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result.contents != null) {
            val barcode = result.contents.toLong()

            // Retrieve all products which match the barcode and update the LiveData.
            viewModel.getBarcode(barcode)

            setFragmentResult(requestKey, bundleOf(bundleKey to result.contents))
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

            // Delete a product upon a swipe to the left, with an option to undo the action.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val productToDelete = products[position]

                backupProducts.clear()
                backupProducts.addAll(products)
                products.removeAt(position)
                ProductAdapter(products).notifyDataSetChanged()

                Snackbar.make(binding.rvProducts, R.string.successful, Snackbar.LENGTH_LONG)
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(snackbar: Snackbar, event: Int) {
                            when (event) {
                                DISMISS_EVENT_ACTION -> {
                                    products.clear()
                                    products.addAll(backupProducts)
                                    ProductAdapter(products).notifyDataSetChanged()
                                } else -> {
                                viewModel.deleteProduct(productToDelete)
                            }
                            }
                        }
                    })
                    .setAction(R.string.undo) { }
                    .show()
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