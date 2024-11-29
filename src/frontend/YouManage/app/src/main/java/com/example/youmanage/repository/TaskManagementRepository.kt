package com.example.youmanage.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
import com.example.youmanage.data.remote.taskmanagement.TaskWebSocket
import com.example.youmanage.factory.WebSocketFactory
import com.example.youmanage.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.HttpException
import javax.inject.Inject

@ActivityScoped
class TaskManagementRepository @Inject constructor(
    private val api: ApiInterface,
    private val webSocketFactory: WebSocketFactory
) {

    private var webSocket: WebSocket? = null


    suspend fun connectToSocket(url: String, liveData: MutableLiveData<Resource<List<Task>>>): Resource<TaskWebSocket> {
        return try {
            val webSocket = webSocketFactory.createWebSocket(url, object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    println("WebSocket opened: ${response.message}")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    try {
                        val taskResponse = Gson().fromJson(text, TaskWebSocket::class.java)

                        if(taskResponse.type == "task_updated"){
                            val updatedTask = taskResponse.task
                            val currentTasks = liveData.value?.data.orEmpty()

                            val updatedTasks = currentTasks.map { task ->
                                if (task.id == updatedTask.id) {
                                    updatedTask
                                } else {
                                    task
                                }
                            }

                            liveData.postValue(Resource.Success(updatedTasks))
                        }
                        else if (taskResponse.type == "task_created") {
                            val createdTask = taskResponse.task
                            val currentTasks = liveData.value?.data.orEmpty()
                            val updatedTasks = currentTasks + createdTask

                            liveData.postValue(Resource.Success(updatedTasks))
                        }
                        Resource.Success(taskResponse)
                    } catch (e: Exception) {
                        Resource.Error("Error parsing response")
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                   // Resource.Error("")
                }
            })

            Resource.Success(TaskWebSocket(
                task = Task(
                    assignee = User("", 0, ""), // Giả sử bạn cần khởi tạo đối tượng User, bạn có thể để trống nếu User có giá trị mặc định.
                    createdAt = "",
                    endDate = "",
                    id = 0,
                    project = 0,
                    startDate = "",
                    status = "",
                    title = "",
                    updatedAt = ""
                ),
                type = ""
            ))
        } catch (e: Exception) {
            // Xử lý lỗi WebSocket
            Resource.Error("Error with WebSocket: ${e.localizedMessage}")
        }
    }

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
        }
        catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
        catch (e: Exception) {
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
            Resource.Success(
                api.postComment(
                    projectId,
                    taskId,
                    comment,
                    authorization
                )
            )
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
            Resource.Success(
                api.updateComment(
                    projectId,
                    taskId,
                    commentId,
                    comment,
                    authorization
                )
            )
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