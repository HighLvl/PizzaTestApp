package ru.cherepanov.apps.pizzatestapp.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Variation(
    val price: Int,
    @SerialName("image_cart")
    val imageCart: String
)