package com.example.youmanage.repository

import android.util.Log
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
import com.example.youmanage.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.HttpException
import javax.inject.Inject

@ActivityScoped
class TaskManagementRepository @Inject constructor(
    private val api: ApiInterface
) {


    suspend fun getTasks(projectId: String, authorization: String): Resource<List<Task>> {
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

    suspend fun updateTaskStatusAndAssignee(
        projectId: String,
        taskId: String,
        task: TaskUpdateStatus,
        authorization: String
    ): Resource<Task> {
        val response = try {
            Resource.Success(
                api.updateTaskStatusAndAssignee(
                    projectId = projectId,
                    taskId = taskId,
                    task = task,
                    authorization = authorization
                )
            )
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
        } catch (e: HttpException) {
            Resource.Error("Task not found")
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
    ): Resource<List<Comment>> {
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
        comment: Content,
        authorization: String
    ): Resource<Comment> {
        val response = try {
            Resource.Success(api.postComment(
                projectId,
                taskId,
                comment,
                authorization))
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
        comment: Content,
        authorization: String
    ): Resource<Comment> {
        val response = try {
            Resource.Success(api.updateComment(projectId, taskId, commentId, comment, authorization))
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
    ) {
        api.deleteComment(projectId, taskId, commentId, authorization)
    }


}