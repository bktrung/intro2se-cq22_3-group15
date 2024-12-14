package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.activitylogs.Activity
import com.example.youmanage.data.remote.authentication.ChangePasswordRequest
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.authentication.Email
import com.example.youmanage.data.remote.authentication.ResetToken
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
import com.example.youmanage.data.remote.authentication.VerifyRequest
import com.example.youmanage.data.remote.chat.Messages
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
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
    ): Message

    @GET("auth/test_token/")
    suspend fun testRefreshToken(
        @Query("refresh") refresh: String,
        @Header("Authorization") authorization: String
    ): String

    @POST("auth/token/refresh/")
    suspend fun refreshAccessToken(
        @Body logout: RefreshToken,
        @Header("Authorization") authorization: String
    ): UserLogInResponse

    @POST("/auth/forgot_password/check_email/")
    suspend fun checkEmail(
        @Body email: Email
    ): Message

    @POST("auth/email_auth/verify/")
    suspend fun verifyOTP(
        @Body verifyRequest: VerifyRequest
    ): Message

    @POST("/auth/email_auth/send_otp/")
    suspend fun sendOTP(
        @Body email: Email
    ): Message

    @POST("/auth/forgot_password/send_otp/")
    suspend fun forgotPasswordSendOTP(
        @Body email: Email
    ): Message

    @POST("/auth/forgot_password/change_password/")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): Message

    @POST("/auth/forgot_password/verify_otp/")
    suspend fun verifyResetPasswordOTP(
        @Body refresh: VerifyRequest
    ): ResetToken

    ///////////////////////////////////////////////////////////////////////////////
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

    @GET("/projects/{projectId}/tasks/")
    suspend fun getTasks(
        @Path("projectId") id: String,
        @Header("Authorization") authorization: String,
    ): List<Task>

    @POST("/projects/{projectId}/tasks/")
    suspend fun createTask(
        @Path("projectId") id: String,
        @Body task: TaskCreate,
        @Header("Authorization") authorization: String
    ): Task

    @GET("/projects/{projectId}/tasks/{taskId}/")
    suspend fun getTask(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Header("Authorization") authorization: String
    ): Task

    @PUT("/projects/{projectId}/tasks/{taskId}/")
    suspend fun updateTask(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Body task: TaskUpdate,
        @Header("Authorization") authorization: String
    ): Task

    @PATCH("/projects/{projectId}/tasks/{taskId}/")
    suspend fun updateTaskStatusAndAssignee(
        @Path("projectId") projectId: String,
        @Body task: TaskUpdateStatus,
        @Path("taskId") taskId: String,
        @Header("Authorization") authorization: String
    ): Task

    @DELETE("/projects/{projectId}/tasks/{taskId}/")
    suspend fun deleteTask(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @GET("/projects/{projectId}/tasks/{taskId}/comments/")
    suspend fun getComments(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Header("Authorization") authorization: String
    ): List<Comment>

    @POST("/projects/{projectId}/tasks/{taskId}/comments/")
    suspend fun postComment(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Body comment: Content,
        @Header("Authorization") authorization: String
    ): Comment

    @GET("/projects/{projectId}/tasks/{taskId}/comments/{commentId}/")
    suspend fun getComment(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Path("commentId") commentId: String,
        @Header("Authorization") authorization: String
    ): Comment

    @PUT("/projects/{projectId}/tasks/{taskId}/comments/{commentId}/")
    suspend fun updateComment(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Path("commentId") commentId: String,
        @Body comment: Content,
        @Header("Authorization") authorization: String
    ): Comment

    @DELETE("/projects/{projectId}/tasks/{taskId}/comments/{commentId}/")
    suspend fun deleteComment(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Path("commentId") commentId: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>


    @GET("/projects/{projectId}/messages/")
    suspend fun getMessage(
        @Path("projectId") projectId: String,
        @Header("Authorization") authorization: String
    ): Messages


    @GET("/users/self/")
    suspend fun getUser(
        @Header("Authorization") authorization: String,
    ): User

    @GET("projects/{projectId}/activities/")
    suspend fun getActivityLogs(
        @Path("projectId") projectId: String,
        @Header("Authorization") authorization: String
    ): List<Activity>

}