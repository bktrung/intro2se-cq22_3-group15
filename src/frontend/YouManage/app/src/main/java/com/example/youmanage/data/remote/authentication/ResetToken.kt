package com.example.youmanage.data.remote.authentication

import com.google.gson.annotations.SerializedName

data class ResetToken(
    val message: String,
    @SerializedName("reset_token")
    val resetToken: String
)
