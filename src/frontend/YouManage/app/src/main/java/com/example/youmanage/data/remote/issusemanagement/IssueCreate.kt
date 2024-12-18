package com.example.youmanage.data.remote.issusemanagement

import com.google.gson.annotations.SerializedName

data class IssueCreate(
    val title: String,
    val description: String,
    val project: Int,
    @SerializedName("assignee_id")
    val assignee: Int?,
    @SerializedName("task_id")
    val task: Int?
)
