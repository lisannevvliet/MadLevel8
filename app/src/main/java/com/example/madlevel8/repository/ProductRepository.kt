package com.example.madlevel8.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.madlevel8.dao.ProductDao
import com.example.madlevel8.database.ProductRoomDatabase
import com.example.madlevel8.model.Product

class ProductRepository(context: Context) {

    private var productDao: ProductDao

    init {
        val productRoomDatabase = ProductRoomDatabase.getDatabase(context)
        productDao = productRoomDatabase!!.productDao()
    }

    // Initialize the LiveData in which the search results will be stored.
    private val _products: MutableLiveData<List<Product>> = MutableLiveData()
    val products: LiveData<List<Product>> get() = _products

    suspend fun getProducts(name: String) { _products.value = productDao.getProducts(name) }

    suspend fun getBarcode(barcode: Long) { _products.value = productDao.getBarcode(barcode) }

    suspend fun getVeganProducts(name: String) { _products.value = productDao.getVeganProducts(name) }

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun deleteAllProducts() = productDao.deleteAllProducts()
}