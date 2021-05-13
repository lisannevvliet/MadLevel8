package com.example.madlevel8.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Checklist(

    @PrimaryKey
    val date: String = "",
    val selected: ArrayList<Boolean> = arrayListOf()
)