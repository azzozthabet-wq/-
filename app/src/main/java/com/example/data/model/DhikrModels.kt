package com.example.data.model

data class DhikrItem(
    val id: String,
    val category: String,
    val text: String,
    val countTarget: Int,
    val virtue: String = ""
)
