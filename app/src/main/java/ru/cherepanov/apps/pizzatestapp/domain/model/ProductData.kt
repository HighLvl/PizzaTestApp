package ru.cherepanov.apps.pizzatestapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductData(
    val categories: List<Category> = emptyList(),
    val productDescriptions: List<ProductDescription> = emptyList()
)

