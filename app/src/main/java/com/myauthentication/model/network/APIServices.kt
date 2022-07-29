package com.myauthentication.model.network

import com.myauthentication.model.data.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface APIServices {

//    @POST("api/contacts")
//    suspend fun postContact(@Body contactDTO: ContactDTO): ContactResponse

    @POST("api/login")
    fun login(@Body loginCredentials: LoginCredentials): Call<LoginResponse>

    @POST("api/register")
    fun register(@Body registerCredentials: RegisterCredentials): Call<RegisterResponse>
    abstract fun postContact(contactDTO: Any): Any

}