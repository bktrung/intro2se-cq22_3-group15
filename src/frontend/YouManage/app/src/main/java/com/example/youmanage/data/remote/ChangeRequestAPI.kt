package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.changerequest.Reply
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChangeRequestAPI {

    @GET("/projects/{project_id}/change-requests/")
    fun getRequest(
        @Path("project_id") project_id: Int,
        @Header("Authorization") authorization: String
    ): List<ChangeRequest>

    @POST("/projects/{project_id}/change-requests/")
    fun createRequest(
        @Path("project_id") projectId: Int,
        @Body request: SendChangeRequest,
        @Header("Authorization") authorization: String
    ): ChangeRequest

    @POST("/projects/{project_id}/change-requests/{change_request_id}/")
    fun replyRequest(
        @Path("project_id") projectId: Int,
        @Path("change_request_id") changeRequestId: Int,
        @Body reply: Reply,
        @Header("Authorization") authorization: String
    ): ChangeRequest


}