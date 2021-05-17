package com.example.madlevel8.dao

import androidx.room.*
import com.example.madlevel8.model.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM Product ORDER BY id DESC")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM Product WHERE vegan ORDER BY id DESC")
    suspend fun getAllVeganProducts(): List<Product>

    @Query("SELECT * FROM Product WHERE name LIKE :name ORDER BY LENGTH(name) ASC")
    suspend fun getProducts(name: String): List<Product>

    @Query("SELECT * FROM Product WHERE name LIKE :name AND vegan ORDER BY LENGTH(name) ASC")
    suspend fun getVeganProducts(name: String): List<Product>

    @Query("SELECT * FROM Product WHERE barcode = :barcode")
    suspend fun getBarcodeProducts(barcode: Long): List<Product>

    @Query("SELECT * FROM Product WHERE barcode = :barcode AND vegan")
    suspend fun getVeganBarcodeProducts(barcode: Long): List<Product>

    @Insert
    suspend fun insertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM Product")
    suspend fun deleteAllProducts()
}