package com.example.youmanage.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.ui.theme.fontFamily
import java.time.Instant
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
                        modifier = Modifier.padding(bottom = 24.dp)
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
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = content,
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp),
                        textAlign = TextAlign.Center
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
fun PasswordTextField(
    content: String,
    onChangeValue: (String) -> Unit,
    placeholderContent: String,
    placeholderColor: Color,
    containerColor: Color
) {

    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.view_password_icon)
    else
        painterResource(id = R.drawable.hide_password_icon)

    TextField(
        value = content,
        textStyle = TextStyle(fontFamily = fontFamily),
        onValueChange = { onChangeValue(it) },
        placeholder = {
            Text(
                text = placeholderContent,
                color = placeholderColor
            )
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    painter = icon,
                    tint = Color.Gray,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    )
}

@Composable
fun TextFieldComponent(
    content: String,
    onChangeValue: (String) -> Unit,
    placeholderContent: String,
    placeholderColor: Color,
    containerColor: Color
) {
    TextField(
        value = content,
        textStyle = TextStyle(fontFamily = fontFamily),
        onValueChange = { onChangeValue(it) },
        placeholder = {
            Text(
                text = placeholderContent,
                color = placeholderColor
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    )
}

@Composable
fun LeadingTextFieldComponent(
    content: String,
    onChangeValue: (String) -> Unit,
    placeholderContent: String,
    placeholderColor: Color,
    containerColor: Color,
    icon: Int
) {
    TextField(
        value = content,
        textStyle = TextStyle(fontFamily = fontFamily),
        leadingIcon = {
            Icon(painter = painterResource(id = icon), contentDescription = "")
        },
        onValueChange = { onChangeValue(it) },
        placeholder = {
            Text(
                text = placeholderContent,
                color = placeholderColor
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
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
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Black)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun <T> ChooseItemDialog(
    title: String = "Choose item",
    showDialog: Boolean = true,
    items: List<T>,
    displayText: (T) -> String, // Hàm để hiển thị tên item
    onDismiss: () -> Unit = {},
    onConfirm: (T) -> Unit,
    itemBackgroundColor: Color = Color.Black.copy(alpha = 0.05f),
    selectedItemBackgroundColor: Color = Color.Cyan
) {
    var isChosenItem by remember { mutableIntStateOf(-1) }

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
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
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
                        color = Color.Black,
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
                                            if (index != isChosenItem) itemBackgroundColor
                                            else selectedItemBackgroundColor
                                        )
                                        .padding(horizontal = 20.dp, vertical = 5.dp)
                                        .clickable {
                                            isChosenItem = index
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
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                        ) {
                            Text("Cancel", fontFamily = fontFamily)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onConfirm(items[isChosenItem])
                            },
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
