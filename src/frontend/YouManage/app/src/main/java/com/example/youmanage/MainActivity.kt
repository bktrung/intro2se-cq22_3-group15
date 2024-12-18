package com.example.youmanage

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.data.remote.activitylogs.ActivityLog
import com.example.youmanage.data.remote.authentication.ChangePasswordRequest
import com.example.youmanage.data.remote.authentication.VerifyRequest
import com.example.youmanage.data.remote.chat.MessageRequest
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.data.remote.taskmanagement.Username
import com.example.youmanage.navigation.RootNavGraph
import com.example.youmanage.screens.activity_logs.ActivityLogsScreen
import com.example.youmanage.screens.chat.ChatScreenWithViewModel
import com.example.youmanage.screens.project_management.ProjectDetailScreen
import com.example.youmanage.screens.task_management.CreateTaskScreen
import com.example.youmanage.screens.task_management.TaskDetailScreen
import com.example.youmanage.screens.task_management.TaskListScreen
import com.example.youmanage.navigation.RootNavGraph
import com.example.youmanage.ui.theme.YouManageTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //val viewModel: ProjectManagementViewModel by viewModels()

        //val viewModel: AuthenticationViewModel by viewModels()

       //val viewModel: TaskManagementViewModel by viewModels()
//
//        val project = ProjectCreate(
//            "My Project Update 1",
//            "2024-04-09",
//            Host(email = "string@gmail.com", username= "string"),
//            "derat"
//        )
//
        //val authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzMyNzg2OTM0LCJpYXQiOjE3MzI2MTQxMzQsImp0aSI6IjEyYTA0MjlmNDI5MjRiYmE5NWIzODdmMDQxOGM5NTlhIiwidXNlcl9pZCI6MX0.QRkVhlw0-LqI62QCakQgO9I3TU_9rRuKnLh2ZEQ7G7c"
//
//        runBlocking {
//            viewModel.deleteProject(
//                id = "3",
//                authorization = authorization
//            )
//        }
//        val api = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzMzMzA3MjE0LCJpYXQiOjE3MzMxMzQ0MTQsImp0aSI6IjBkYjVjMTI1NTg1YzQ0OWI5YmFjZTIwNzg1NWY0YjM0IiwidXNlcl9pZCI6MX0.uDa7jZdy_YFrer59vwVoLbAIw_-lTD_OdEqI_LFj7gI"
//        val url = "${WEB_SOCKET}chat/1/?token=$api"
//
//        val viewModel : ChatViewModel by viewModels()
//
//        viewModel.connectToSocket(url)
//
//        viewModel.sendMessage(
//            MessageRequest(
//                "Rank con"
//            )
//        )

//        viewModel.verifyResetPasswordOTP(
//            VerifyRequest(
//                "duonghuutuong0712@gmail.com",
//                "320760"
//            )
//        )

//        viewModel.changePassword(
//            ChangePasswordRequest(
//                "string",
//                "string",
//                "40782c00-09ce-4e07-8315-fa8e3891a572"
//            )
//        )

            setContent {
                YouManageTheme {

                //val nav = rememberNavController()

//                viewModel.createTask(
//                    projectId = "1",
//                    task = TaskCreate(
//                        assigneeId = 2,
//                        endDate = "2024-12-25",
//                        startDate = "2024-12-07",
//                        title = "Hello"
//                    ),
//                    authorization = authorization
//                )

//                CreateTaskScreen(
//                    navHostController = nav,
//                    projectId = "1")

              //  CreateTaskScreen()
                //TaskListScreen()
            //ProjectDetailScreen()
                //FindUserScreen()
               RootNavGraph()
                //TestPieChart()

               // TaskDetailScreen()
                //TaskDetailScreen(projectId = "1", taskId = "1")

                //viewModel.sendOTP(SendOTPRequest("duonghuutuong0712@gmail.com"))

                //ChatScreenWithViewModel()
                //ActivityLogsScreen(projectId = "1", token = "your_token")
             //}
//            ActivityLogsScreen(
//                projectId = "12345",  // Giả sử ID của project
//                token = "fake_token",  // Giả sử token xác thực
//                onBackClick = {}
//            )
        }
    }
}





