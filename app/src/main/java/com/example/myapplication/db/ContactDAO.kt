package com.example.myapplication.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDAO {

    @Query("SELECT * from Contacts")
    fun getAllContacts() : Flow<List<Contacts>>


    @Insert
    suspend fun addContact(contacts: Contacts)

    @Update
    fun updateContact(contacts: Contacts): Int

    @Delete
    fun deleteContact(contacts: Contacts)

}