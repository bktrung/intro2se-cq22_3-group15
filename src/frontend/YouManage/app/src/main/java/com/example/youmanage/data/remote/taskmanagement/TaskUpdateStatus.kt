package com.example.youmanage.data.remote.taskmanagement
import com.google.gson.annotations.SerializedName

data class TaskUpdateStatus(
    @SerializedName("assignee_id")
    val assigneeId: Int? = null,
    val status: String
)