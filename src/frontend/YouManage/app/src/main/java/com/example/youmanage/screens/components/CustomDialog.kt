package com.example.youmanage.screens.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.screens.project_management.MemberItem
import com.example.youmanage.ui.theme.fontFamily
import com.example.youmanage.utils.randomVibrantLightColor
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AlertDialog(
    title: String,
    content: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = content,
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Cancel", fontFamily = fontFamily)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "OK",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = fontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(
    title: String,
    content: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.error_img),
                        contentDescription = "Error",
                        modifier = Modifier.size(70.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = title,
                        fontFamily = fontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = content,
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Cancel", fontFamily = fontFamily)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("OK",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = fontFamily)
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentTimeMillis = System.currentTimeMillis()

    val todayMillis = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= todayMillis
        }
    }


    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentTimeMillis,
        selectableDates = selectableDates
    )

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())


    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.ofEpochMilli(millis)
                        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        val formattedDate = formatter.format(date)
                        onDateSelected(formattedDate)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "OK",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                titleContentColor = MaterialTheme.colorScheme.primary,         // Màu cho tiêu đề
                headlineContentColor = MaterialTheme.colorScheme.primary,     // Màu cho tiêu đề chính (headline)
                weekdayContentColor = MaterialTheme.colorScheme.primary,      // Màu cho ngày trong tuần
                dayContentColor = MaterialTheme.colorScheme.primary,          // Màu cho các ngày
                todayContentColor = MaterialTheme.colorScheme.primary,          // Màu cho ngày hiện tại
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,  // Màu cho ngày được chọn
                yearContentColor = MaterialTheme.colorScheme.primary,         // Màu cho các năm
                currentYearContentColor = Color.Red,    // Màu cho năm hiện tại
                selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,  // Màu cho năm được chọn,
                disabledDayContentColor = Color.Gray,
                dateTextFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    disabledTextColor = Color.Gray,
                    errorTextColor = MaterialTheme.colorScheme.onError)
            )
        )
    }
}


@Composable
fun <T> ChooseItemDialog(
    title: String = "Choose item",
    showDialog: Boolean = true,
    items: List<T>,
    checkItems: List<Boolean> = emptyList(),
    displayText: (T) -> String,
    onDismiss: () -> Unit = {},
    onConfirm: (T) -> Unit,
    itemBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    isReset: Boolean = false,
    selectedItemBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {

    var isChosenItem by remember { mutableIntStateOf(-1) }

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontFamily = fontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(items.size) { index ->
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(

                                            if (checkItems.isNotEmpty() && !checkItems[index]) MaterialTheme.colorScheme.primaryContainer
                                            else if (index != isChosenItem) itemBackgroundColor
                                            else selectedItemBackgroundColor
                                        )
                                        .padding(horizontal = 20.dp, vertical = 5.dp)
                                        .clickable {
                                            if (checkItems.isNotEmpty() && checkItems[index]) {
                                                isChosenItem = index
                                            }

                                            if (!isReset) {
                                                isChosenItem = index
                                            }

                                        }
                                ) {

                                    if (items[index] is User) {
                                        Image(
                                            painter = painterResource(id = R.drawable.avatar),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                        )
                                    }
                                    Text(
                                        displayText(items[index]),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                onDismiss()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Cancel", fontFamily = fontFamily)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (isChosenItem >= 0) {
                                    onConfirm(items[isChosenItem])
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "OK",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = fontFamily
                            )
                        }
                    }
                }
            }
        }
    }

    if (isReset) isChosenItem = -1
}


@Composable
fun CreateRoleDialog(
    title: String = "Create Role",
    showDialog: Boolean = true,
    onDismiss: () -> Unit = {},
    nameOg: String = "",
    descriptionOg: String = "",
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    if (showDialog) {

        var name by remember(nameOg) { mutableStateOf(nameOg) }
        var description by remember(descriptionOg) { mutableStateOf(descriptionOg) }

        Dialog(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .widthIn(min = 300.dp, max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column {
                    Text(
                        "Role name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            onNameChange(it)
                        },
                        label = { Text("Role name") },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary)


                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column {
                    Text(
                        "Role description",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            onDescriptionChange(it)
                        },
                        label = { Text("Role description") },
                        maxLines = 5,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            "Cancel",
                            fontFamily = fontFamily,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onNameChange(if (name != nameOg) name else nameOg)
                            onDescriptionChange(if (description != descriptionOg) description else descriptionOg)
                            onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "OK",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = fontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChangeRequestDialog(
    title: String,
    content: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    if (showDialog) {

        var description by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = content,
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            onDescriptionChange(it)
                        },
                        label = { Text("Enter description") },
                        maxLines = Int.MAX_VALUE
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                        ) {
                            Text("Cancel", fontFamily = fontFamily)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("OK", color = Color.White, fontFamily = fontFamily)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddMemberDialog(
    title: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (MemberItem) -> Unit
) {
    if (showDialog) {

        var username by remember {
            mutableStateOf("")
        }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                ),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            "Username",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        LeadingTextFieldComponent(
                            content = username,
                            onChangeValue = { username = it },
                            placeholderContent = "Username",
                            placeholderColor = Color.Gray,
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            icon = R.drawable.member_icon
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                MemberItem(
                                    username,
                                    randomVibrantLightColor(),
                                    R.drawable.avatar
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            "Add",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ReplyChangeRequest(
    title: String = "Your change requset ",
    showDialog: Boolean = true,
    onDismiss: () -> Unit = {},
    onAprrove: () -> Unit = {},
    onDecline: () -> Unit = {}
) {
    if (showDialog) {

        var reason by remember { mutableStateOf("") }
        var openReasonTextField by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .widthIn(min = 300.dp, max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            openReasonTextField = !openReasonTextField
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Text("Decline", fontFamily = fontFamily)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAprrove,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                        ,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Approve",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = fontFamily)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(
                    visible = openReasonTextField,
                    enter = slideInVertically() + expandVertically(),
                    exit = slideOutVertically() + shrinkVertically()
                ) {
                    Column {
                        Text(
                            "Declined Reason:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = reason,
                            onValueChange = {
                                reason = it
                            },
                            label = { Text("Declined Reason") },
                            maxLines = 5,
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.primary)
                        )

                        Button(
                            onClick = onDecline,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Ok",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = fontFamily)
                        }
                    }
                }
            }
        }
    }
}

