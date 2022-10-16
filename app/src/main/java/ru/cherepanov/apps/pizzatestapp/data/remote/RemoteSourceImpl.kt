package ru.cherepanov.apps.pizzatestapp.data.remote

import ru.cherepanov.apps.pizzatestapp.domain.model.Category
import ru.cherepanov.apps.pizzatestapp.domain.model.ProductData
import ru.cherepanov.apps.pizzatestapp.domain.model.ProductDescription
import ru.cherepanov.apps.pizzatestapp.domain.RemoteSource
import javax.inject.Inject

class RemoteSourceImpl @Inject constructor(private val webService: PizzaWebService) : RemoteSource {
    override suspend fun getProductData(): ProductData {
        val goods = webService.getGoods()
        val productDescriptions = goods.map {
            val variation = it.variations.first()
            ProductDescription(
                price = variation.price,
                description = it.description,
                title = it.name,
                imageUrl = variation.imageCart
            )
        }

        val categories = goods.asSequence().mapIndexed { index, product ->
            index to product
        }.groupBy { (_, product) ->
            product.category
        }.map { (categoryName, indexToProductPairs) ->
            Category(
                name = categoryName,
                productIndex = indexToProductPairs.first().first
            )
        }
        return ProductData(categories, productDescriptions)
    }
}