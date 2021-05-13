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
import com.example.madlevel8.repository.ProductRepository
import com.example.madlevel8.vm.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val bundleKey= ""
const val requestKey = ""

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var productRepository: ProductRepository

    private val products = arrayListOf<Product>()
    private val backupProducts = arrayListOf<Product>()
    private val productAdapter = ProductAdapter(products, ::onClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chipGroup.visibility =  View.INVISIBLE

        // Initialize the ProductRepository.
        productRepository = ProductRepository(requireContext())

        binding.btnScan.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)

            // Remove the default text.
            integrator.setPrompt("")
            // Disable the beep upon a successful scan.
            integrator.setBeepEnabled(false)
            // Start the barcode scanner.
            integrator.initiateScan()
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addProductFragment)
        }

        binding.tvNothingFound.visibility =  View.INVISIBLE


        // Initialize the recycler view with a linear layout manager, adapter.
        binding.rvProducts.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvProducts.adapter = productAdapter
        createItemTouchHelper().attachToRecyclerView(binding.rvProducts)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                CoroutineScope(Dispatchers.Main).launch {
                    val products = withContext(Dispatchers.IO) { productRepository.getProducts("%$query%") }

                    this@HomeFragment.products.clear()
                    this@HomeFragment.products.addAll(products)

                    if (products.isEmpty()) {

                        setFragmentResult(requestKey, bundleOf(bundleKey to query))

                        binding.tvNothingFound.visibility =  View.VISIBLE
                        binding.chipGroup.visibility =  View.INVISIBLE
                    } else {
                        binding.tvNothingFound.visibility =  View.INVISIBLE
                        binding.chipGroup.visibility =  View.VISIBLE
                    }

                    productAdapter.notifyDataSetChanged()
                }
                return false
            }

            // Do not update the RecyclerView while typing.
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result.contents != null) {
            Snackbar.make(binding.btnScan, "Scanned: " + result.contents, Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(binding.btnScan, "Cancelled", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    // Enable touch behavior (like swipe and move) on each ViewHolder, and use callbacks to signal when a user is performing these actions.
    private fun createItemTouchHelper(): ItemTouchHelper {

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
                productAdapter.notifyDataSetChanged()

                Snackbar.make(binding.rvProducts, R.string.successful, Snackbar.LENGTH_LONG)
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(snackbar: Snackbar, event: Int) {
                            when (event) {
                                DISMISS_EVENT_ACTION -> {
                                    products.clear()
                                    products.addAll(backupProducts)
                                    productAdapter.notifyDataSetChanged()
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

    private fun onClick(product: Product) {
        Snackbar.make(binding.rvProducts, "This product is: ${product.name}", Snackbar.LENGTH_LONG).show()
    }
}