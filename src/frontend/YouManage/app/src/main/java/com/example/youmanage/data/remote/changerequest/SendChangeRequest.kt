package com.example.youmanage.data.remote.changerequest

import com.google.gson.annotations.SerializedName

data class SendChangeRequest(
    val description: String? = null,
    @SerializedName("new_data")
    val newData: Any? = null,
    @SerializedName("request_type")
    val requestType: String? = null,
    @SerializedName("target_table")
    val targetTable: String? = null,
    @SerializedName("target_table_id")
    val targetTableId: Int? = null
)