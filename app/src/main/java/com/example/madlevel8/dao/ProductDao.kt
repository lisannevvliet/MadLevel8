package com.example.madlevel8.dao

import androidx.room.*
import com.example.madlevel8.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM Product WHERE name LIKE :name")
    suspend fun getProducts(name: String): List<Product>

    @Query("SELECT * FROM Product WHERE barcode = :barcode")
    suspend fun getBarcode(barcode: Long): List<Product>

    @Query("SELECT * FROM Product WHERE name LIKE :name AND vegan")
    suspend fun getVeganProducts(name: String): List<Product>

    @Insert
    suspend fun insertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM Product")
    suspend fun deleteAllProducts()
}