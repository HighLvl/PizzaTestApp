package ru.cherepanov.apps.pizzatestapp.domain

import ru.cherepanov.apps.pizzatestapp.domain.model.ProductData

interface LocalSource {
    suspend fun getProductData(): ProductData
    suspend fun saveProductData(productData: ProductData)
}