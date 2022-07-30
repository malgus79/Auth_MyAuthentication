package com.myauthentication.model.network

import com.myauthentication.model.data.LoginCredentials
import com.myauthentication.model.data.LoginResponse
import com.myauthentication.model.data.RegisterCredentials
import com.myauthentication.model.data.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface APIServices {

    @POST("api/login")
    fun login(@Body loginCredentials: LoginCredentials): Call<LoginResponse>

    @POST("api/register")
    fun register(@Body registerCredentials: RegisterCredentials): Call<RegisterResponse>

}