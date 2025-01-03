package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.issusemanagement.Issue
import com.example.youmanage.data.remote.issusemanagement.IssueCreate
import com.example.youmanage.data.remote.issusemanagement.IssueUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface IssueAPI {
    @GET("/projects/{projectId}/issues/")
    suspend fun getIssues(
        @Path("projectId") projectId: String,
        @Header("Authorization") authorization: String
    ): List<Issue>

    @POST("/projects/{projectId}/issues/")
    suspend fun createIssue(
        @Path("projectId") projectId: String,
        @Body issue: IssueCreate,
        @Header("Authorization") authorization: String
    ): Issue

    @GET("/projects/{projectId}/issues/{issueId}/")
    suspend fun getIssue(
        @Path("projectId") projectId: String,
        @Path("issueId") issueId: String,
        @Header("Authorization") authorization: String
    ): Issue

    @PATCH("/projects/{projectId}/issues/{issueId}/")
    suspend fun updateIssue(
        @Path("projectId") projectId: String,
        @Path("issueId") issueId: String,
        @Body issueUpdate: IssueUpdate,
        @Header("Authorization") authorization: String
    ): Issue

    @DELETE("/projects/{projectId}/issues/{issueId}/")
    suspend fun deleteIssue(
        @Path("projectId") projectId: String,
        @Path("issueId") issueId: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>
}