package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.projectmanagement.Assign
import com.example.youmanage.data.remote.projectmanagement.Role
import com.example.youmanage.data.remote.projectmanagement.RoleRequest
import com.example.youmanage.data.remote.taskmanagement.Detail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoleAPI {
    @GET("/projects/{pk}/roles/")
    suspend fun getRoles(
        @Path("pk") projectId: String,
        @Header("Authorization") authorization: String
    ): List<Role>

    @GET("/projects/{project_id}/roles/{pk}/")
    suspend fun getRole(
        @Path("project_id") projectId: String,
        @Path("pk") roleId: String,
        @Header("Authorization") authorization: String
    ): Role

    @POST("/projects/{pk}/roles/")
    suspend fun createRole(
        @Path("pk") projectId: String,
        @Body role: RoleRequest,
        @Header("Authorization") authorization: String
    ): Role

    @PUT("/projects/{project_id}/roles/{pk}/")
    suspend fun updateRole(
        @Path("project_id") projectId: String,
        @Path("pk") roleId: String,
        @Body role: RoleRequest,
        @Header("Authorization") authorization: String)

    @DELETE("/projects/{project_id}/roles/{pk}/")
    suspend fun deleteRole(
        @Path("project_id") projectId: String,
        @Path("pk") roleId: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @POST("/projects/{project_id}/roles/{pk}/{action}/")
    suspend fun assignRole(
        @Path("project_id") projectId: String,
        @Path("pk") roleId: String,
        @Path("action") action: String,
        @Body member: Assign
    ): Detail
}