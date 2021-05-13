package com.example.madlevel8.repository

import android.content.Context
import com.example.madlevel8.dao.ProductDao
import com.example.madlevel8.database.ProductRoomDatabase
import com.example.madlevel8.model.Product

class ProductRepository(context: Context) {

    private var productDao: ProductDao

    init {
        val productRoomDatabase = ProductRoomDatabase.getDatabase(context)
        productDao = productRoomDatabase!!.productDao()
    }

    suspend fun getProducts(name: String): List<Product> = productDao.getProducts(name)

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun deleteAllProducts() = productDao.deleteAllProducts()
}