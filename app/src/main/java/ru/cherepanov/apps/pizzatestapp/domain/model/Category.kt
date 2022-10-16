package ru.cherepanov.apps.pizzatestapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val name: String,
    val productIndex: Int
)