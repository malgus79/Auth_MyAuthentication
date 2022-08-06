package com.myauthentication.core

import android.content.Context
import android.content.SharedPreferences

class Prefs(val context: Context) {
    val SHARED_NAME = "MyPrefs"
    val SHARED_TOKEN = "UserToken"

    private val myPrefs: SharedPreferences = context.getSharedPreferences(SHARED_NAME, 0)

    //Save Token
    fun saveToken(token: String) {
        myPrefs.edit().putString(SHARED_TOKEN, token).apply()
    }

    //Get Token
    fun getToken(): String {
        return myPrefs.getString(SHARED_TOKEN, "")!!
    }

    //Delete Token
    fun deleteToken() {
        myPrefs.edit().putString(SHARED_TOKEN, "").apply()
    }
}