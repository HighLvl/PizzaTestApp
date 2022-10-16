package ru.cherepanov.apps.pizzatestapp.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.create
import ru.cherepanov.apps.pizzatestapp.BuildConfig
import ru.cherepanov.apps.pizzatestapp.data.local.LocalSourceImpl
import ru.cherepanov.apps.pizzatestapp.data.remote.PizzaWebService
import ru.cherepanov.apps.pizzatestapp.data.remote.RemoteSourceImpl
import ru.cherepanov.apps.pizzatestapp.domain.LocalSource
import ru.cherepanov.apps.pizzatestapp.domain.RemoteSource
import javax.inject.Singleton

@Module(includes = [NetworkModule.BindsModule::class])
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideDictionaryWebService(
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory
    ): PizzaWebService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(DICT_SERVICE_URL)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(
                Json {
                    ignoreUnknownKeys = true
                }.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }).build()
    }

    @Provides
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return CoroutineCallAdapterFactory()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindsModule {
        @Binds
        fun bindRemoteSource(remoteSource: RemoteSourceImpl): RemoteSource

        @Binds
        fun bindLocalSource(localSource: LocalSourceImpl): LocalSource
    }

    private companion object {
        const val DICT_SERVICE_URL = "http://18.184.158.90:7999/"
    }
}