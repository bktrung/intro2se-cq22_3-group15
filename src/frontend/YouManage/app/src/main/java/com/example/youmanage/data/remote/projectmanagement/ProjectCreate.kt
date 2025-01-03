package com.example.youmanage.data.remote.projectmanagement

import com.google.gson.annotations.SerializedName

data class ProjectCreate(
    val description: String,
    @SerializedName("duedate")
    val dueDate: String,
    val name: String
)