package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.projectmanagement.GanttChartData
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.IsHost
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.projectmanagement.UserId
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.taskmanagement.Username
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProjectAPI {
    @GET("/projects/")
    suspend fun getProjectList(
        @Header("Authorization") authorization: String
    ): Projects

    @POST("/projects/")
    suspend fun createProject(
        @Body project: ProjectCreate,
        @Header("Authorization") authorization: String
    ): Project

    @GET("/projects/{id}/")
    suspend fun getProject(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    ): Project

    @PUT("/projects/{id}/")
    suspend fun updateFullProject(
        @Path("id") id: String,
        @Body project: ProjectCreate,
        @Header("Authorization") authorization: String
    ): Project

    @PATCH("/projects/{id}/")
    suspend fun updateProject(
        @Path("id") id: String,
        @Body project: ProjectCreate,
        @Header("Authorization") authorization: String
    ): Project

    @DELETE("/projects/{projectId}/")
    suspend fun deleteProject(
        @Path("projectId") id: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @POST("/projects/{projectId}/members/add/")
    suspend fun addMember(
        @Path("projectId") id: String,
        @Body member: Username,
        @Header("Authorization") authentication: String
    ): Detail

    @POST("/projects/{projectId}/members/remove/")
    suspend fun removeMember(
        @Path("projectId") id: String,
        @Body memberId: Id,
        @Header("Authorization") authentication: String
    ): Detail

    @GET("/projects/{projectId}/members/")
    suspend fun getMembers(
        @Path("projectId") id: String,
        @Header("Authorization") authentication: String
    ): List<User>

    @GET("/projects/{projectId}/gantt-chart/")
    suspend fun getGanttChartData(
        @Path("projectId") id: String,
        @Header("Authorization") authentication: String
    ): List<GanttChartData>

    @GET("/users/self/")
    suspend fun getUser(
        @Header("Authorization") authorization: String,
    ): User

    @POST("/projects/{projectId}/quit/")
    suspend fun quitProject(
        @Path("projectId") id: String,
        @Header("Authorization") authentication: String
    ): Detail

    @POST("/projects/{projectId}/empower/")
    suspend fun empower(
        @Path("projectId") id: String,
        @Body userId: UserId,
        @Header("Authorization") authentication: String
    ): Message

    @GET("/projects/{projectId}/check-host")
    suspend fun isHost(
        @Path("projectId") id: String,
        @Header("Authorization") authentication: String
    ): IsHost
}