package ru.cherepanov.apps.pizzatestapp.domain

import ru.cherepanov.apps.pizzatestapp.domain.model.ProductDataResult
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) {
    suspend fun getProductData(): Result<ProductDataResult> = kotlin.runCatching {
        try {
            val productData = remoteSource.getProductData()
            localSource.saveProductData(productData)
            ProductDataResult(
                false,
                productData
            )

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            ProductDataResult(
                isCachedData = true,
                productData = localSource.getProductData()
            )
        }
    }
}