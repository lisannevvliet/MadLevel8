package com.example.madlevel8.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(

    val name: String,
    val vegan: Boolean,
    val barcode: Long? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)