package com.example.youmanage.data.remote.taskmanagement

import com.example.youmanage.data.remote.projectmanagement.User
import com.google.gson.annotations.SerializedName

data class Comment(
    val author: User,
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    val id: Int,
    @SerializedName("updated_at")
    val updatedAt: String
)