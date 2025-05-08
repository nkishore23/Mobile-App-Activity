package com.example.myapplication

import android.content.Context
import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.db.ContactDAO
import com.example.myapplication.db.Contacts

@Database(version = 1, entities = [Contacts::class])
abstract class ContactDatabase : RoomDatabase(){

    abstract fun contactsDao() : ContactDAO

        companion object {

        private var INSTANCE : ContactDatabase? = null

        fun getInstance(context : Context) : ContactDatabase {
            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java,
                        "contact_db"
                    ).build()
                }
                INSTANCE = instance
                return instance

            }
        }


    }
}