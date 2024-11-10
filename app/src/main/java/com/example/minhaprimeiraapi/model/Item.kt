package com.example.minhaprimeiraapi.model

import java.util.Date

data class Item(
    val id: String,
    val value: ItemValue
)

data class ItemValue(
    val id: String,
    val imageUrl: String,
    val year: String,
    val name: String,
    val licence: String,
    val place: ItemLocation?,
    val date: Date?
)

data class ItemLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)
