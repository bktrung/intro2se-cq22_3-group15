package com.example.youmanage.screens.project_management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youmanage.R
import com.example.youmanage.screens.components.PieChart
import com.example.youmanage.screens.components.pieChartInput

@Preview
@Composable
fun ProjectDetailScreen(
    backgroundColor: Color = Color(0xffBAE5F5),
    modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopBar()
        },
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = 24.dp)

    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize() // Đảm bảo Box chiếm toàn bộ diện tích
                .padding(paddingValues)
                .background(backgroundColor)// Thêm padding từ Scaffold

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                   /// .verticalScroll(scrollState)
            ) {
                PieChart(
                    input = pieChartInput,
                    modifier = Modifier.padding(36.dp)
                )

                DescriptionSection()

                Button(
                    onClick = { /* Thêm hành động cho nút View Project */ },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(horizontal = 36.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF251034))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(text = "Task List", color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    Color.Gray,
                                    shape = CircleShape
                                ) // Màu nền và hình dạng tròn
                                .padding(4.dp), // Khoảng cách giữa mũi tên và viền tròn
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "➜", modifier = Modifier)
                        }

                    }

                }

                //MembersSection()

            }
        }
    }
}


@Composable
fun TopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xffBAE5F5))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow_icon),
                contentDescription = "Back"
            )
        }
        Text(
            text = "Project Detail",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Back"
            )
        }
    }
}

@Composable
fun DescriptionSection(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
    ) {
        Text(
            text = "Description",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "  This project has a task management, gantt chart, have five members, " +
                    "use kotlin and jetpack compose to develop UI... Read more",
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = Color.Black.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MembersSection(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(horizontal = 36.dp)
    ) {
        Text(
            text = "Members",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            items(5){
                MemberItem(
                    MemberItem(
                        username = "Derat",
                        backgroundColor = Color.Yellow,
                        avatar = R.drawable.avatar
                    ),
                    onDelete = {
                        //members = members.filter { i -> i.username != it }
                    },
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }
    }


}