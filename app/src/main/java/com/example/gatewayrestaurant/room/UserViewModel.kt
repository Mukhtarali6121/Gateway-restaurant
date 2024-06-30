package com.example.gatewayrestaurant.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
class UserViewModel(private val database: AppDatabase) : ViewModel() {

    fun addUser(name: String, email: String) {
        viewModelScope.launch {
            GlobalScope.launch(Dispatchers.IO) {

                val user = User(name = name, email = email)
                database.userDao().insertUser(user)
            }
        }
    }

    fun getUsers() {
        viewModelScope.launch {
            val users = database.userDao().getAllUsers()
            // Handle the list of users
        }
    }
}

