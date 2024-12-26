package com.example.youmanage.data.remote.chat

import com.example.youmanage.data.remote.projectmanagement.User
import com.google.gson.annotations.SerializedName

data class Message(
    val author: User,
    val content: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val id: Int,
    val project: Int,
    val timestamp: String
)