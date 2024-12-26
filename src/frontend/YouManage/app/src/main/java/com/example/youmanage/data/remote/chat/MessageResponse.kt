package com.example.youmanage.data.remote.chat

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    val id: Int,
    val author: String,
    val content: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val timestamp: String
)
