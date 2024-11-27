package com.example.youmanage.repository

import android.util.Log
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.Role
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

    suspend fun getProjectList(authorization: String): Resource<Projects> {
        val response = try {
            Resource.Success(api.getProjectList(authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun createProject(project: ProjectCreate, authorization: String): Resource<Project> {
        val response = try {
            Resource.Success(api.createProject(project = project, authorization = authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun getProject(id: String, authorization: String): Resource<Project> {
        val response = try {
            Resource.Success(api.getProject(id = id, authorization = authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun updateFullProject(
        id: String,
        project: ProjectCreate,
        authorization: String
    ): Resource<Project> {
        val response = try {
            Resource.Success(api.updateFullProject(id, project, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
        return response
    }

    suspend fun updateProject(
        id: String,
        project: ProjectCreate,
        authorization: String
    ): Resource<Project> {
        val response = try {
            Resource.Success(api.updateProject(id, project, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun deleteProject(id: String, authorization: String) {
        try {
            api.deleteProject(id, authorization)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addMember(id: String, member: Username, authorization: String): Resource<Detail> {
        val response = try {
            Resource.Success( api.addMember(id, member, authorization))
        }
        catch (e: HttpException) {
            if (e.code() == 400) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody?.let { JSONObject(it).getString("detail") }
                Resource.Error("$errorMessage")
            } else if (e.code() == 404) {
                Resource.Error("User not found")
            }else {
                Resource.Error("HTTP Error: ${e.code()}")
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun removeMember(id: String, memberId: Id, authorization: String): Resource<Detail> {
        val response = try {
            api.removeMember(id, memberId, authorization)
            Resource.Success(api.removeMember(id, memberId, authorization))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody?.let { JSONObject(it).getString("detail") }
                Resource.Error("$errorMessage")
            } else if (e.code() == 404) {
                Resource.Error("User not found")
            }else {
                Resource.Error("HTTP Error: ${e.code()}")
            }
        }
        return response
    }


    suspend fun getMembers(
        id: String,
        authorization: String
    ): Resource<List<User>> {
        val response = try {
            Resource.Success(api.getProject(id, authorization).members)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

}