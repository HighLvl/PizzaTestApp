package ru.cherepanov.apps.pizzatestapp.data.remote

import retrofit2.http.GET
import ru.cherepanov.apps.pizzatestapp.data.remote.model.Product

interface PizzaWebService {
    @GET("/goods")
    suspend fun getGoods(): List<Product>
}