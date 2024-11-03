package com.example.youmanage

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.youmanage.navigation.RootNavGraph
import com.example.youmanage.screens.project_management.AddProjectScreen
import com.example.youmanage.ui.theme.YouManageTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


//        val viewModel: ProjectManagementViewModel by viewModels()
//
//        val project = ProjectCreate(
//            "My Project Update 1",
//            "2024-04-09",
//            Host(email = "string@gmail.com", username= "string"),
//            "derat"
//        )
//
//        val authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzI5MjU5MzQ5LCJpYXQiOjE3MjkwODY1NDksImp0aSI6IjdiMmU1NzVkNjc1NDQ5NjViOWNmYTY2MGU3NWEzMzA1IiwidXNlcl9pZCI6MX0.wITO6OwnhH5smemrfuj6aPeBwrbcRFQc_QOZoF9pgcQ"
//
//        runBlocking {
//            viewModel.deleteProject(
//                id = "3",
//                authorization = authorization
//            )
//        }



       // viewModel.getProjectList(authorization = authorization)

        setContent {
            YouManageTheme {
                AddProjectScreen()

            }
        }
    }
}





