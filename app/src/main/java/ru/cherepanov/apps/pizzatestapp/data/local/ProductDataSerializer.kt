package ru.cherepanov.apps.pizzatestapp.data.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.cherepanov.apps.pizzatestapp.domain.model.ProductData
import java.io.InputStream
import java.io.OutputStream

class ProductDataSerializer(
    private val stringFormat: StringFormat = Json,
) : Serializer<ProductData> {
    override val defaultValue: ProductData
        get() = ProductData()

    override suspend fun readFrom(input: InputStream): ProductData {
        try {
            val bytes = input.readBytes()
            val string = bytes.decodeToString()
            return stringFormat.decodeFromString(string)
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read stored data", e)
        }
    }

    override suspend fun writeTo(t: ProductData, output: OutputStream) {
        val string = stringFormat.encodeToString(t)
        val bytes = string.encodeToByteArray()
        output.write(bytes)
    }
}