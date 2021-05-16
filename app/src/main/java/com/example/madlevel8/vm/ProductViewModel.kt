package com.example.madlevel8.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.madlevel8.model.Product
import com.example.madlevel8.repository.ProductRepository
import kotlinx.coroutines.*

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepository = ProductRepository(application.applicationContext)

    // Retrieve the LiveData with the search results from the ProductRepository.
    val products = productRepository.products

    fun getProducts(name: String) {
        CoroutineScope(Dispatchers.Main).launch { productRepository.getProducts("%$name%") }
    }

    fun getBarcode(barcode: Long) {
        CoroutineScope(Dispatchers.Main).launch { productRepository.getBarcode(barcode) }
    }

    fun getVeganProducts(name: String) {
        CoroutineScope(Dispatchers.Main).launch { productRepository.getVeganProducts("%$name%")}
    }

    fun insertProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch { productRepository.insertProduct(product) }
    }

    fun deleteProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch { productRepository.deleteProduct(product) }
    }

    fun deleteAllProducts() {
        CoroutineScope(Dispatchers.IO).launch { productRepository.deleteAllProducts() }
    }
}