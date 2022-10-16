package ru.cherepanov.apps.pizzatestapp.data.local

import android.content.Context
import androidx.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import ru.cherepanov.apps.pizzatestapp.domain.LocalSource
import ru.cherepanov.apps.pizzatestapp.domain.model.ProductData
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by dataStore(
    fileName = "cache.json",
    serializer = ProductDataSerializer()
)

class LocalSourceImpl @Inject constructor(@ApplicationContext context: Context) : LocalSource {
    private val dataStore = context.dataStore

    override suspend fun getProductData(): ProductData {
        val productData = dataStore.data.first()
        if (productData == ProductData()) {
            throw IOException()
        }
        return productData
    }

    override suspend fun saveProductData(productData: ProductData) {
        dataStore.updateData { productData }
    }
}