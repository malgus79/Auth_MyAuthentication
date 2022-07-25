package com.myauthentication.core

import android.app.Application

class MyAuthenticationApp : Application(){

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }

    companion object{
        lateinit var prefs: Prefs
    }
}