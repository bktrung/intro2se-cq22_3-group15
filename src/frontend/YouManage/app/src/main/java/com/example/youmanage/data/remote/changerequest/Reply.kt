package com.example.youmanage.data.remote.changerequest

import com.google.gson.annotations.SerializedName

data class Reply(
    val action: String? = null,
    @SerializedName("declined_reason")
    val declinedReason: String? = null,
)
