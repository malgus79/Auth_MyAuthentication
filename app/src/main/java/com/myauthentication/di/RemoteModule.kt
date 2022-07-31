package com.myauthentication.di

import com.myauthentication.model.network.APIServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Singleton
    @Provides
    fun providesAPIService(retrofit: Retrofit): APIServices {
        return retrofit.create(APIServices::class.java)
    }

    @Singleton
    @Provides
    fun providesRetrofitClient(): Retrofit {

        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
    }
}

const val BASE_URL = "https://ongapi.alkemy.org/"


//const val BASE_URL = "http://ongapi.alkemy.org/"
//
////Create logging interceptor
//private val loggingInterceptor = HttpLoggingInterceptor()
//    .setLevel(HttpLoggingInterceptor.Level.BODY)
//
////Create OkHttp Client
//private val client = OkHttpClient.Builder()
//    .addInterceptor(loggingInterceptor)
//
////Create Retrofit Builder
//private val retrofit = Retrofit.Builder()
//    .baseUrl(BASE_URL)
//    .addConverterFactory(GsonConverterFactory.create())
//    .client(client.build())
//    .build()
