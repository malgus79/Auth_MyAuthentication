package com.myauthentication.repository

import com.myauthentication.model.data.*
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

    fun logIn(loginCredentials: LoginCredentials): Call<LoginResponse>{
        return APIService.login(loginCredentials)
    }

    fun register(registerCredentials: RegisterCredentials): Call<RegisterResponse> {
        return APIService.register(registerCredentials)
    }

}