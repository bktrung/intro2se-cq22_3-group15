package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.data.remote.authentication.LogoutResponse
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.Role
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("auth/signup/")
    suspend fun signUp(
        @Body user: UserSignUp
    ): UserSignUpResponse

    @POST("auth/login/")
    suspend fun logIn(
        @Body user: UserLogIn
    ): UserLogInResponse

    @POST("auth/google-login/")
    suspend fun logInWithGoogle(
        @Body user: UserGoogleLogIn
    ): UserLogInResponse

    @POST("auth/logout/")
    suspend fun logOut(
        @Body logoutRequest: RefreshToken,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String
    ): LogoutResponse

    @GET("auth/test_token/")
    suspend fun testRefreshToken(
        @Query("refresh") refresh: String,
        @Header("Authorization") authorization: String
    ):String

    @POST("auth/token/refresh/")
    suspend fun refreshAccessToken(
        @Body logout: RefreshToken,
        @Header("Authorization") authorization: String
    ):UserLogInResponse

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

    @DELETE("/projects/{id}/")
    suspend fun deleteProject(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    )

    @POST("/projects/{id}/members/{action}/")
    suspend fun createMember(
        @Path("id") id: String,
        @Path("action") action: String,
        @Body role: Role
    )




}