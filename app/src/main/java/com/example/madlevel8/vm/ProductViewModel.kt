package com.example.madlevel8.vm

import android.app.Application
import android.view.View
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.madlevel8.R
import com.example.madlevel8.adapter.ProductAdapter
import com.example.madlevel8.model.Product
import com.example.madlevel8.repository.ProductRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepository = ProductRepository(application.applicationContext)

    private val products = arrayListOf<Product>()
    private val backupProducts = arrayListOf<Product>()

    val productAdapter = ProductAdapter(products)

    // Retrieve all (vegan) products from the database (which match the search query or barcode).
    fun getProducts(name: String, tvNoProductsFound: TextView, checked: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            val products =
                    // Retrieve all (vegan) products.
                    if (name.isBlank()) {
                        if (checked) {
                            withContext(Dispatchers.IO) { productRepository.getAllVeganProducts() }
                        } else {
                            withContext(Dispatchers.IO) { productRepository.getAllProducts() }
                        }
                    }
                    // Retrieve all (vegan) products which match the barcode.
                    else if (name.all { it.isDigit() }) {
                        val barcode = name.toLong()
                        if (checked) {
                            withContext(Dispatchers.IO) { productRepository.getVeganBarcodeProducts(barcode) }
                        } else {
                            withContext(Dispatchers.IO) { productRepository.getBarcodeProducts(barcode) }
                        }
                    // Retrieve all (vegan) products which match the search query.
                    } else if (checked) {
                        withContext(Dispatchers.IO) { productRepository.getVeganProducts("%$name%") }
                    } else {
                        withContext(Dispatchers.IO) { productRepository.getProducts("%$name%") }
                    }

            // Fill the products list with the database results.
            this@ProductViewModel.products.clear()
            this@ProductViewModel.products.addAll(products)

            // Show the no products found error if the products list is empty.
            if (products.isEmpty()) {
                tvNoProductsFound.visibility = View.VISIBLE
            } else {
                tvNoProductsFound.visibility = View.INVISIBLE
            }

            // Update the RecyclerView.
            productAdapter.notifyDataSetChanged()
        }
    }

    // Add the product to the database.
    fun insertProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch { productRepository.insertProduct(product) }
    }

    // Delete the product from the database upon a swipe to the left, with an option to undo the action.
    fun deleteProduct(position: Int, rvProducts: RecyclerView) {
        // Backup the products, in case the undo button is clicked.
        backupProducts.clear()
        backupProducts.addAll(products)

        // Store the position of the product to delete.
        val product = products[position]

        // Delete the product from the RecyclerView.
        products.removeAt(position)

        // Update the RecyclerView.
        productAdapter.notifyDataSetChanged()

        // Show a Snackbar message which says that the product has been deleted, with an undo button next to it.
        Snackbar.make(rvProducts, getApplication<Application>().resources.getString(R.string.deleted, product.name), Snackbar.LENGTH_LONG)
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(snackbar: Snackbar, event: Int) {
                    when (event) {
                        DISMISS_EVENT_ACTION -> {
                            // Restore the backup of the products.
                            products.clear()
                            products.addAll(backupProducts)
                            // Update the RecyclerView.
                            productAdapter.notifyDataSetChanged()
                        } else -> {
                        // Permanently delete the product from the database.
                        CoroutineScope(Dispatchers.IO).launch { productRepository.deleteProduct(product) }
                    }
                    }
                }
            })
            .setAction(R.string.undo) { }
            .show()
    }

    // Delete all products in the database.
    fun deleteAllProducts() {
        CoroutineScope(Dispatchers.IO).launch { productRepository.deleteAllProducts() }
    }
}