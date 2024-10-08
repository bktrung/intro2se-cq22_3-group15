package com.example.youmanage.data.remote.authentication

import com.google.gson.annotations.SerializedName

data class UserLogIn(
    @SerializedName("username_or_email")
    val username: String,
    val password: String
)