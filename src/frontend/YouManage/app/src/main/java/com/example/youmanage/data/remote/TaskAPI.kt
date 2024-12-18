package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.chat.Messages
import com.example.youmanage.data.remote.projectmanagement.Progress
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
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

interface TaskAPI {

    @POST("/projects/{projectId}/progress/track/")
    suspend fun getProgressTracker(
        @Path("projectId") projectId: String,
        @Header("Authorization") authorization: String
    ): Progress

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
        @Query("cursor") cursor: String? = null,
        @Header("Authorization") authorization: String
    ): Messages

}