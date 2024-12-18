package com.example.youmanage.data.remote.projectmanagement

import com.google.gson.annotations.SerializedName

data class RoleRequest(
    @SerializedName("role_name")
    val name: String,
    val description: String
)
