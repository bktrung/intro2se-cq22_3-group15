package com.example.youmanage.repository

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.projectmanagement.Assign
import com.example.youmanage.data.remote.projectmanagement.GanttChartData
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.IsHost
import com.example.youmanage.data.remote.projectmanagement.Progress
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.Role
import com.example.youmanage.data.remote.projectmanagement.RoleRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.projectmanagement.UserId
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.taskmanagement.Username
import com.example.youmanage.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (e: HttpException) {
        handleHttpException(e)
    } catch (e: Exception) {
        e.printStackTrace()
        Resource.Error(e.message.toString())
    }
}

fun <T> handleHttpException(e: HttpException): Resource.Error<T> {
    return when (e.code()) {
        400 -> {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                // Parse the JSON and extract the "error" value
                val jsonObject = JSONObject(errorBody ?: "{}")
                jsonObject.getString("error")
            } catch (ex: Exception) {
                "Invalid error format"
            }
            Resource.Error(errorMessage)
        }

        404 -> Resource.Error("User not found")
        else -> Resource.Error("HTTP Error: ${e.code()}")
    }
}

@ActivityScoped
class ProjectManagementRepository @Inject constructor(
    private val api: ApiInterface
) {


    suspend fun getProjectList(authorization: String): Resource<Projects> =
        safeApiCall { api.getProjectList(authorization) }

    suspend fun createProject(project: ProjectCreate, authorization: String): Resource<Project> =
        safeApiCall { api.createProject(project, authorization) }

    suspend fun getProject(id: String, authorization: String): Resource<Project> =
        safeApiCall { api.getProject(id, authorization) }

    suspend fun updateFullProject(
        id: String,
        project: ProjectCreate,
        authorization: String
    ): Resource<Project> =
        safeApiCall { api.updateFullProject(id, project, authorization) }

    suspend fun updateProject(
        id: String,
        project: ProjectCreate,
        authorization: String
    ): Resource<Project> =
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

    suspend fun getRoles(
        projectId: String,
        authorization: String
    ): Resource<List<Role>> =
        safeApiCall {
            api.getRoles(projectId, authorization)
        }

    suspend fun createRole(
        projectId: String,
        role: RoleRequest,
        authorization: String
    ): Resource<Role> =
        safeApiCall {
            api.createRole(
                projectId,
                role,
                authorization
            )
        }


    suspend fun getRole(
        projectId: String,
        roleId: String,
        authorization: String
    ): Resource<Role> = safeApiCall {
        api.getRole(
            projectId,
            roleId,
            authorization
        )
    }

    suspend fun deleteRole(
        projectId: String,
        roleId: String, authorization: String
    ): Resource<String>  {
        return try {
            api.deleteRole(
                projectId,
                roleId,
                authorization
            )
            Resource.Success("Delete Role Successful")
        } catch (e: Exception) {
            Resource.Error("Delete Role Failed")
        }
    }

    suspend fun getRolesOfMember(
        projectId: String,
        memberId: String,
        authorization: String
    ): Resource<List<Role>> = safeApiCall {
        api.getRolesOfMember(
            projectId,
            memberId,
            authorization
        )
    }

    suspend fun updateRole(
        projectId: String,
        roleId: String,
        role: RoleRequest,
        authorization: String
    ): Resource<Role> = safeApiCall {
        api.updateRole(
            projectId,
            roleId,
            role,
            authorization
        )
    }

    suspend fun assignRole(
        projectId: String,
        roleId: String,
        action: String,
        member: Assign,
        authorization: String
    ): Resource<Detail> = safeApiCall {
        api.assignRole(
            projectId,
            roleId,
            action,
            member,
            authorization
        )
    }

    suspend fun getNonMember(
        projectId: String,
        roleId: String,
        authorization: String
    ): Resource<List<User>> = safeApiCall {
        api.getNonMembers(
            projectId,
            roleId,
            authorization
        )
    }

    suspend fun getGanttChartData(id: String, authorization: String): Resource<List<GanttChartData>> =
        safeApiCall { api.getGanttChartData(id,authorization) }

    suspend fun quitProject(
        id: String,
        authorization: String
    ): Resource<Detail> = safeApiCall {
        api.quitProject(
            id,
            authorization
        )
    }

    suspend fun empower(
        id: String,
        userId: UserId,
        authorization: String
    ): Resource<Message> = safeApiCall {
        api.empower(
            id,
            userId,
            authorization
        )
    }

    suspend fun isHost(
        id: String,
        authorization: String
    ): Resource<IsHost> = safeApiCall {
        api.isHost(id, authorization)
    }

}
