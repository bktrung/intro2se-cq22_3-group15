package com.example.youmanage.data.remote.authentication

import com.google.gson.annotations.SerializedName

data class ChangePassword(
    @SerializedName("current_password")
    val currentPassword: String? = null,
    @SerializedName("new_password")
    val newPassword: String? = null,
    @SerializedName("confirm_password")
    val confirmPassword: String? = null,
)
