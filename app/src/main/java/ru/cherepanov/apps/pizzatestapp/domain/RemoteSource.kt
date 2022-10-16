package ru.cherepanov.apps.pizzatestapp.domain

import ru.cherepanov.apps.pizzatestapp.domain.model.ProductData

interface RemoteSource {
    suspend fun getProductData(): ProductData
}