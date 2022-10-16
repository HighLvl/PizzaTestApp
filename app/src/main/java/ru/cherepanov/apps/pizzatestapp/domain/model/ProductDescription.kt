package ru.cherepanov.apps.pizzatestapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDescription(
    val price: Int,
    val description: String,
    val title: String,
    val imageUrl: String
)