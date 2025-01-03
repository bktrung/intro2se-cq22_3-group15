package com.example.youmanage.data.remote.projectmanagement

import com.google.gson.annotations.SerializedName

data class Role(
    val description: String,
    val id: Int,
    val project: Int,
    @SerializedName("role_name")
    val name: String
)