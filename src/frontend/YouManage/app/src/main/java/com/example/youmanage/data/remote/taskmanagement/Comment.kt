package com.example.youmanage.data.remote.taskmanagement

import com.example.youmanage.data.remote.projectmanagement.User
import com.google.gson.annotations.SerializedName

data class Comment(
    val author: User = User(),
    val content: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    val id: Int = 0,
    @SerializedName("updated_at")
    val updatedAt: String = ""
)