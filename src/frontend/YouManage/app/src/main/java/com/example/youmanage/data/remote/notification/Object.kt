package com.example.youmanage.data.remote.notification

import com.google.gson.annotations.SerializedName

data class Object(
    @SerializedName("project_id")
    val projectId: Int? = null,
    @SerializedName("task_id")
    val taskId: Int? = null,
    @SerializedName("issue_id")
    val issueId: Int? = null,
    @SerializedName("change_request_id")
    val changeRequestId: Int? = null
)
