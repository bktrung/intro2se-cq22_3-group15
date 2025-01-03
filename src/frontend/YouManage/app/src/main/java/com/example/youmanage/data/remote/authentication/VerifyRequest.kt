package com.example.youmanage.data.remote.authentication

data class VerifyRequest(
    val email: String,
    val otp: String
)
