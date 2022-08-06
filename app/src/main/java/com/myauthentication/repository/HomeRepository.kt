package com.myauthentication.repository

import com.myauthentication.model.data.LoginCredentials
import com.myauthentication.model.data.LoginResponse
import com.myauthentication.model.data.RegisterCredentials
import com.myauthentication.model.data.RegisterResponse
import com.myauthentication.model.network.APIServices
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(private val APIService: APIServices) {

    //Calls APIServices postContact function
//    suspend fun postContact(contactDTO: ContactDTO): ContactResponse {
//        return APIService.postContact(contactDTO)
//    }

    //Method for Login
    fun logIn(loginCredentials: LoginCredentials): Call<LoginResponse>{
        return APIService.login(loginCredentials)
    }

    //Method for Sign Up
    fun register(registerCredentials: RegisterCredentials): Call<RegisterResponse> {
        return APIService.register(registerCredentials)
    }

}