package com.example.youmanage.repository

import android.util.Log
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.Role
import com.example.youmanage.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ProjectManagementRepository @Inject constructor(
    private val api: ApiInterface
) {

    suspend fun getProjectList(authorization: String) : Resource<Projects> {
        val response = try {
            Resource.Success(api.getProjectList(authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun createProject(project: ProjectCreate, authorization: String) : Resource<Project> {
        val response = try {
            Resource.Success(api.createProject(project = project, authorization = authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun getProject(id: String, authorization: String) : Resource<Project> {
        val response = try {
            Resource.Success(api.getProject(id = id, authorization = authorization))
        } catch (e: Exception) {
            e.printStackTrace()
             Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun updateFullProject(id: String, project: ProjectCreate, authorization: String) : Resource<Project> {
        val response = try {
            Resource.Success(api.updateFullProject(id, project, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
        return response
    }

    suspend fun updateProject(id: String, project: ProjectCreate, authorization: String) : Resource<Project> {
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

    suspend fun createMember(id: String, action: String, role: Role) {
        try {
            api.createMember(id, action, role)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}