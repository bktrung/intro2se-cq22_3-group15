package com.example.youmanage.data.remote.authentication

import com.google.gson.annotations.SerializedName

data class UserGoogleLogIn(
    @SerializedName("id_token")
    val idToken: String,
)
