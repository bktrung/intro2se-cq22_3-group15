package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.changerequest.ChangeRequests
import com.example.youmanage.data.remote.changerequest.Reply
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChangeRequestAPI {

    @GET("/projects/{project_id}/change-requests/")
    suspend fun getChangeRequests(
        @Path("project_id") projectId: Int,
        @Query("cursor") cursor: String? = null,
        @Query("status") status: String? = null,
        @Header("Authorization") authorization: String
    ): ChangeRequests

    @GET("/projects/{project_id}/change-requests/view/{change_request_id}")
    suspend fun getChangeRequest(
        @Path("project_id") projectId: Int,
        @Path("change_request_id") changeRequestId: Int,
        @Header("Authorization") authorization: String
    ): ChangeRequest

    @POST("/projects/{project_id}/change-requests/")
    suspend fun createRequest(
        @Path("project_id") projectId: Int,
        @Body request: SendChangeRequest,
        @Header("Authorization") authorization: String
    ): ChangeRequest

    @POST("/projects/{project_id}/change-requests/{change_request_id}/")
    suspend fun replyRequest(
        @Path("project_id") projectId: Int,
        @Path("change_request_id") changeRequestId: Int,
        @Body reply: Reply,
        @Header("Authorization") authorization: String
    ): ChangeRequest

}