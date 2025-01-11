package com.example.youmanage.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youmanage.R
import com.example.youmanage.ui.theme.fontFamily
import com.example.youmanage.utils.randomAvatar


@Composable
fun PasswordTextField(
    content: String,
    onChangeValue: (String) -> Unit,
    placeholderContent: String,
    placeholderColor: Color,
    containerColor: Color,
    imeAction: ImeAction,
    onDone: () -> Unit,
    onNext:() -> Unit,
) {

    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.view_password_icon)
    else
        painterResource(id = R.drawable.hide_password_icon)

    val focusManager = LocalFocusManager.current


        TextField(
            value = content,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground
            ),
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onNext() },
                onDone = { onDone() }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            shape = RoundedCornerShape(10.dp)
        )

}

@Composable
fun TextFieldComponent(
    content: String,
    onChangeValue: (String) -> Unit,
    placeholderContent: String,
    placeholderColor: Color,
    containerColor: Color,
    imeAction: ImeAction,
    onDone: () -> Unit,
    onNext:() -> Unit,
) {

    TextField(
        value = content,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground
        ),
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
            unfocusedIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onNext() },
            onDone = { onDone() }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        shape = RoundedCornerShape(10.dp)

    )
}

@Composable
fun LeadingTextFieldComponent(
    content: String,
    onChangeValue: (String) -> Unit,
    placeholderContent: String,
    placeholderColor: Color,
    containerColor: Color,
    icon: Int,
    imeAction: ImeAction,
    onDone: () -> Unit,
    onNext:() -> Unit,
) {
    val focusManager = LocalFocusManager.current

        TextField(
            value = content,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground
                )
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
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { onNext() },
                onDone = { onDone() }
            ),
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

}



@Composable
fun DatePickerField(
    label: String,
    date: String,
    onDateClick: () -> Unit,
    iconResource: Int,
    placeholder: String,
    containerColor: Color,
    imeAction: ImeAction,
    onDone: () -> Unit,
    onNext:() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        val focusManager = LocalFocusManager.current
        Box(
            modifier = Modifier.clickable {
                onDateClick()
                focusManager.clearFocus()
            }
        ) {
            TextField(
                value = date,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = iconResource),
                        contentDescription = null,
                        modifier = Modifier.clickable { onDateClick() },
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
                keyboardActions = KeyboardActions(
                    onNext = { onNext() },
                    onDone = { onDone() }
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
fun AssigneeSelector(
    label: String,
    avatarRes: Int,
    userId: Int = -1,
    username: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clickable { onClick() }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = randomAvatar(userId)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = username,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            }
        }
    }
}


@Composable
fun TaskSelector(
    label: String,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clickable { onClick() }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun DropdownStatusSelector(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    cornerRadius: Dp = 10.dp,
    padding: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = textColor
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = textColor
            )
        }
    }
}