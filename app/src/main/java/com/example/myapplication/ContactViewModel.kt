package com.example.myapplication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.db.Contacts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactViewModel(): ViewModel() {

    val allContacts: Flow<List<Contacts>> = MyApplication.db.contactsDao().getAllContacts()
    var contactDao = MyApplication.db.contactsDao()

    fun insertContact(contact: Contacts) {
        viewModelScope.launch {
            contactDao.addContact(contact)
        }
    }

    fun deleteContact(contact: Contacts) {
        viewModelScope.launch {
            contactDao.deleteContact(contact)
        }
    }


}