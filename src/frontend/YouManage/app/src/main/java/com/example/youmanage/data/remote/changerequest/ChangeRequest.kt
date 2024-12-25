package com.example.youmanage.data.remote.changerequest

import com.example.youmanage.data.remote.taskmanagement.Task
import com.google.gson.annotations.SerializedName

data class ChangeRequest(
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("declined_reason")
    val declinedReason: String? = null,
    val description: String? = null,
    val id: Int? = null,
    @SerializedName("new_data")
    val newData: Task? = null,
    val project: Int? = null,
    @SerializedName("requestType")
    val requestType: String? = null,
    val requester: Int? = null,
    @SerializedName("reviewed_at")
    val reviewedAt: String? = null,
    @SerializedName("reviewed_by")
    val reviewedBy: Int,
    val status: String,
    @SerializedName("target_table")
    val targetTable: String,
    @SerializedName("target_table_id")
    val targetTableId: Int
)