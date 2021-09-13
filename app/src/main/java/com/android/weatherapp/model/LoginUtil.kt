package com.android.weatherapp.model

object LoginUtil {

    private val userList = listOf("Davao", "Rizal")

    fun validateLoginField(
        username: String,
        password: String
    ): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            return false
        }
        if (username in userList) {
            return false
        }
        if (password.count() < 4) {
            return false
        }
        return true
    }
}