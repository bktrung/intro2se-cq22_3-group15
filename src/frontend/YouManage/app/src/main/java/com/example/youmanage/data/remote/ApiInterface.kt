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
import com.example.youmanage.data.remote.issusemanagement.Issue
import com.example.youmanage.data.remote.issusemanagement.IssueCreate
import com.example.youmanage.data.remote.issusemanagement.IssueUpdate
import com.example.youmanage.data.remote.projectmanagement.Assign
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.Progress
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.Role
import com.example.youmanage.data.remote.projectmanagement.RoleRequest
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

interface ApiInterface: AuthenticateAPI, ProjectAPI, TaskAPI, IssueAPI, RoleAPI {

    @GET("projects/{projectId}/activities/")
    suspend fun getActivityLogs(
        @Path("projectId") projectId: String,
        @Header("Authorization") authorization: String
    ): List<Activity>

}