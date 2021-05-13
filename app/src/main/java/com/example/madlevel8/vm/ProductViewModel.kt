package com.example.madlevel8.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.madlevel8.model.Product
import com.example.madlevel8.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepository = ProductRepository(application.applicationContext)

    fun getProducts(name: String) {
        CoroutineScope(Dispatchers.IO).launch { productRepository.getProducts(name) }
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