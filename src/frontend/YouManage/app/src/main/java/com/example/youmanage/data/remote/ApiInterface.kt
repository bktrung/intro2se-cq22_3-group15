package com.example.youmanage.data.remote

interface ApiInterface: AuthenticateAPI, ProjectAPI, TaskAPI, IssueAPI, RoleAPI, ActivitiesAPI {

//    @GET("projects/{projectId}/activities/")
//    suspend fun getActivityLogs(
//        @Path("projectId") projectId: String,
//        @Header("Authorization") authorization: String
//    ): List<Activity>

}