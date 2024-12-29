package com.example.youmanage.data.remote.changerequest

data class ChangeRequests(
    val next: String?,
    val previous: String?,
    val results: List<ChangeRequest>
)
