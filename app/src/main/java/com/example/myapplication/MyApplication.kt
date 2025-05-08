package com.example.myapplication

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        db = ContactDatabase.getInstance(this)
    }

    companion object {
         lateinit var db: ContactDatabase;
    }
}