package ru.cherepanov.apps.pizzatestapp.domain.model

data class ProductDataResult(
    val isCachedData: Boolean,
    val productData: ProductData
)