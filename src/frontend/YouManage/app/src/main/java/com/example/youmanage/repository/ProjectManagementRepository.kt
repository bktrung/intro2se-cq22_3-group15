package com.example.youmanage.repository

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.Progress
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.taskmanagement.Username
import com.example.youmanage.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@ActivityScoped
class ProjectManagementRepository @Inject constructor(
    private val api: ApiInterface
) {

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(apiCall())
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }

    private fun <T> handleHttpException(e: HttpException): Resource.Error<T> {
        return when (e.code()) {
            400 -> {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody?.let { JSONObject(it).getString("detail") } ?: "Bad Request"
                Resource.Error(errorMessage)
            }
            404 -> Resource.Error("User not found")
            else -> Resource.Error("HTTP Error: ${e.code()}")
        }
    }

    suspend fun getProjectList(authorization: String): Resource<Projects> =
        safeApiCall { api.getProjectList(authorization) }

    suspend fun createProject(project: ProjectCreate, authorization: String): Resource<Project> =
        safeApiCall { api.createProject(project, authorization) }

    suspend fun getProject(id: String, authorization: String): Resource<Project> =
        safeApiCall { api.getProject(id, authorization) }

    suspend fun updateFullProject(id: String, project: ProjectCreate, authorization: String): Resource<Project> =
        safeApiCall { api.updateFullProject(id, project, authorization) }

    suspend fun updateProject(id: String, project: ProjectCreate, authorization: String): Resource<Project> =
        safeApiCall { api.updateProject(id, project, authorization) }

    suspend fun getProgressTrack(projectId: String, authorization: String): Resource<Progress> =
        safeApiCall { api.getProgressTracker(projectId, authorization) }

    suspend fun deleteProject(id: String, authorization: String): Resource<String> {
        return try {
            api.deleteProject(id = id, authorization = authorization)
            Resource.Success("Delete Successful")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }

    suspend fun addMember(id: String, member: Username, authorization: String): Resource<Detail> =
        safeApiCall { api.addMember(id, member, authorization) }

    suspend fun removeMember(id: String, memberId: Id, authorization: String): Resource<Detail> =
        safeApiCall { api.removeMember(id, memberId, authorization) }

    suspend fun getMembers(id: String, authorization: String): Resource<List<User>> =
        safeApiCall { api.getProject(id, authorization).members }
}
