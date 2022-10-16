package ru.cherepanov.apps.pizzatestapp.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val category: String,
    val name: String,
    val description: String,
    val variations: List<Variation>,

    )

