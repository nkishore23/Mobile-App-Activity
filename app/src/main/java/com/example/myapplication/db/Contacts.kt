package com.example.myapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contacts(@PrimaryKey(autoGenerate = true) val id:Int =0, val name:String, val mobileNumber:String, val emailAddress:String, val description:String)
