package com.example.youmanage.data.remote.websocket

import com.example.youmanage.data.remote.projectmanagement.User
import com.google.gson.annotations.SerializedName

data class MemberObject(
    @SerializedName("affected_members")
    val affectedMembers: List<User>,
    @SerializedName("project_id")
    val projectId: Int
)