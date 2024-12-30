package com.example.youmanage.data.remote.notification

import com.google.gson.annotations.SerializedName

data class Notification(
    val body: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    val id: Int? = null,
    @SerializedName("is_read")
    val isRead: Boolean? = null,
    val title: String? = null,
    @SerializedName("object")
    val objectContent: Object? = null
)