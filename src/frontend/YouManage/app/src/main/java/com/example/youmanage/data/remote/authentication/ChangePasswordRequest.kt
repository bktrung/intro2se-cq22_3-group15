package com.example.youmanage.data.remote.authentication

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    val email: String,
    val otp: String,
    @SerializedName("new_password")
    val newPassword: String,
)
