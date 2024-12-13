package com.example.youmanage.data.remote.issusemanagement

import com.google.gson.annotations.SerializedName

data class IssueUpdate(
    val title: String,
    val description: String,
    val status: String = "",
    val project: Int,
    @SerializedName("assignee_id")
    val assignee: Int?,
    @SerializedName("task_id")
    val task: Int?
)
