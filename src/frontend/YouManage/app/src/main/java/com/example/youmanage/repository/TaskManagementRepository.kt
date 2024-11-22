package com.example.youmanage.repository

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class TaskManagementRepository @Inject constructor(
    private val api: ApiInterface
) {

    suspend fun getTasks(projectId: String, authorization: String): Resource<ArrayList<Task>> {
        val response = try {
            Resource.Success(api.getTasks(projectId, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun createTask(
        projectId: String,
        task: TaskCreate,
        authorization: String
    ): Resource<Task> {
        val response = try {
            Resource.Success(api.createTask(projectId, task, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun getTask(projectId: String, taskId: String, authorization: String): Resource<Task> {
        val response = try {
            Resource.Success(api.getTask(projectId, taskId, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun updateTask(
        projectId: String,
        taskId: String,
        task: TaskUpdate,
        authorization: String
    ): Resource<Task> {
        val response = try {
            Resource.Success(api.updateTask(projectId, taskId, task, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun updateTaskStatus(
        projectId: String,
        taskId: String,
        authorization: String
    ): Resource<Task> {
        val response = try {
            Resource.Success(api.updateTaskStatus(projectId, taskId, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun deleteTask(
        projectId: String,
        taskId: String,
        authorization: String
    ): Resource<String> {
        val response = try {
            api.deleteTask(projectId, taskId, authorization)
            Resource.Success("Task has been deleted successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun getComments(
        projectId: String,
        taskId: String,
        authorization: String
    ): Resource<ArrayList<Comment>> {
        val response = try {
            Resource.Success(api.getComments(projectId, taskId, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun postComment(
        projectId: String,
        taskId: String,
        comment: String,
        authorization: String
    ): Resource<Comment> {
        val response = try {
            Resource.Success(api.postComment(projectId, taskId, comment, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun getComment(
        projectId: String,
        taskId: String,
        commentId: String,
        authorization: String
    ): Resource<Comment> {
        val response = try {
            Resource.Success(api.getComment(projectId, taskId, commentId, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun updateComment(
        projectId: String,
        taskId: String,
        commentId: String,
        comment: Content
    ): Resource<Comment> {
        val response = try {
            Resource.Success(api.updateComment(projectId, taskId, commentId, comment))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun deleteComment(
        projectId: String,
        taskId: String,
        commentId: String,
        authorization: String
    ): Resource<String> {
        val response = try {
            api.deleteComment(projectId, taskId, commentId, authorization)
            Resource.Success("Comment has been deleted successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

}