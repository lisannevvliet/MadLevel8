package com.example.madlevel8.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Marker(

    @PrimaryKey
    val position: String,
    val title: String,
    val address: String
)