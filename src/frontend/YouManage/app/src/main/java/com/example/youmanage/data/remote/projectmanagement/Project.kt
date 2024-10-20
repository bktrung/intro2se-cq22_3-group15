package com.example.youmanage.data.remote.projectmanagement

import com.google.gson.annotations.SerializedName

data class Project(
    @SerializedName("create_at")
    val createdAt: String,
    val description: String,
    @SerializedName("duedate")
    val dueDate: String,
    val host: User,
    val id: Int,
    val members: List<User>,
    val name: String,
    @SerializedName("updated_at")
    val updatedAt: String
)