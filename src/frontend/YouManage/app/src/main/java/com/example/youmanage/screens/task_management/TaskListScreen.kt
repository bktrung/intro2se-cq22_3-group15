package com.example.youmanage.screens.task_management

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youmanage.R


@Preview
@Composable
fun TaskListScreen(modifier: Modifier = Modifier) {

    var isSelectedButton by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        topBar = {
            TopBar()
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xffF26A6A))
                .padding(paddingValues)
                .padding(top = 24.dp)
        ) {
            Column {
                ButtonSection(
                    isSelectedButton = isSelectedButton,
                    onClick = {
                        isSelectedButton = it
                    }
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(5) {
                        TaskItem()

                    }
                }
            }
        }
    }

}

@Composable
fun TaskItem(modifier: Modifier = Modifier) {
    Card(
        onClick = { /*TODO*/ },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)

    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(50.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Task 1",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f) // Cho phép Text chiếm không gian còn lại mà không lấn vào Box
                        .wrapContentWidth(Alignment.Start)
                        .padding(end = 5.dp)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.LightGray)
                ) {
                    Text(
                        text = "High",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)

                    )
                }

            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        "Derat", fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.comment_icon),
                        contentDescription = "Comment",
                        tint = Color.Black
                    )
                    Text("2", fontSize = 15.sp, fontWeight = FontWeight.Medium)

                    Icon(
                        painter = painterResource(id = R.drawable.calendar_icon),
                        contentDescription = "Deadline",
                        tint = Color.Black
                    )
                    Text("Oct 11", fontSize = 15.sp, fontWeight = FontWeight.Medium)

                }
            }
        }
    }
}

@Composable
fun TopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xffF26A6A))
            .padding(24.dp)
            .padding(top = 24.dp),
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
            text = "Task List",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.size(30.dp))
    }
}

@Composable
fun ButtonSection(
    isSelectedButton: Int,
    onClick: (Int) -> Unit
) {
    val listButton = listOf("Done", "In Progress", "To Do")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listButton.forEachIndexed { index, name ->
            TaskListButton(
                name = name,
                contentColor = if (index == isSelectedButton) Color(0xffF26A6A) else Color.Black,
                containerColor = if (index == isSelectedButton) Color.Black else Color.Transparent,
                onClick = { onClick(index) }
            )
        }
    }
}


@Composable
fun TaskListButton(
    name: String,
    contentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier.border(
            2.dp,
            contentColor,
            RoundedCornerShape(30.dp)
        )

    ) {
        Text(
            name, fontWeight = FontWeight.Bold, fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

