package com.example.youmanage.screens.authetication

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.authentication.Email
import com.example.youmanage.data.remote.authentication.ResetToken
import com.example.youmanage.data.remote.authentication.VerifyRequest
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.screens.components.KeyboardStatus
import com.example.youmanage.screens.components.keyboardAsState
import com.example.youmanage.screens.issue_management.TopBar
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import kotlinx.coroutines.delay


@Composable
fun OTPTextField(
    otpLength: Int,
    onOtpChanged: (String) -> Unit,
    modifier: Modifier
) {

    var otp by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardState = keyboardAsState(KeyboardStatus.Closed)

    val isShowWarning by remember(keyboardState) {
        derivedStateOf {
            keyboardState.value == KeyboardStatus.Closed && otp.length != otpLength
        }
    }

    val focusRequester = remember {
        FocusRequester()
    }

    BasicTextField(
        value = otp,
        modifier = modifier
            .focusRequester(focusRequester)
            .windowInsetsPadding(WindowInsets.ime)
            .windowInsetsPadding(WindowInsets.systemBars),
        onValueChange = { input ->

            if (input.length <= otpLength && input.all { it.isDigit() }) {
                otp = input
                onOtpChanged(otp)
            }
        },
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(otpLength) { index ->
                    val char = when {
                        index < otp.length -> otp[index].toString()
                        else -> ""
                    }
                    val isFocused = index == otp.length
                    OTPCell(
                        char = char,
                        isFocus = isFocused,
                        isShowWarning = isShowWarning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
    )

    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

@Preview
@Composable
fun OTPCell(
    char: String = "",
    isFocus: Boolean = false,
    isShowWarning: Boolean = false,
    modifier: Modifier = Modifier
) {

    val borderColor = if (isShowWarning) {
        MaterialTheme.colorScheme.error
    } else if (isFocus) {
        Color.Green
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Surface(
        modifier = modifier
            .aspectRatio(0.7f)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.wrapContentSize(align = Alignment.Center)
        )
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun OTPVerificationScreen(
    expiredTime: Int,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    email: String = "",
    from: String,
    onNavigateBack: () -> Unit,
    onVerifySuccess: (String) -> Unit
) {

    val verifyOTPResponse = when (from) {
        "1" -> authenticationViewModel.verifyOTPResponse.observeAsState().value
        "2" -> authenticationViewModel.verifyResetPasswordOTP.observeAsState().value
        else -> null
    }

    val scrollState = rememberScrollState()

    var timeInSeconds by remember { mutableIntStateOf(expiredTime) }
    var isRunning by remember { mutableStateOf(true) }

    var minutes by remember { mutableIntStateOf(timeInSeconds / 60) }
    var seconds by remember { mutableIntStateOf(timeInSeconds % 60) }

    var otp by remember { mutableStateOf("") }

    var openErrorDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(verifyOTPResponse) {
        when (from) {
            "1" -> {
                if (verifyOTPResponse is Resource.Success) {
                    Toast.makeText(
                        context,
                        "Verification completed successfully!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onVerifySuccess(otp)
                } else if (verifyOTPResponse is Resource.Error) {
                    openErrorDialog = true
                }
            }

            "2" -> {
                if (verifyOTPResponse is Resource.Success) {
                    Toast.makeText(
                        context,
                        "Verification completed successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val resetToken = verifyOTPResponse.data as? ResetToken
                    onVerifySuccess(resetToken?.resetToken.toString())
                } else if (verifyOTPResponse is Resource.Error) {
                    openErrorDialog = true
                }
            }
        }

    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            timeInSeconds -= 1
            minutes = timeInSeconds / 60
            seconds = timeInSeconds % 60

            if (timeInSeconds <= 0) isRunning = false
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),
        topBar = {
            com.example.youmanage.screens.project_management.TopBar(
                title = "",
                color = Color.Transparent,
                trailing = {
                    Box(
                        modifier = Modifier.size(24.dp)
                    )
                },
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (email.isNotEmpty()) {
                            when (from) {
                                "1" -> {
                                    authenticationViewModel.verifyOTP(
                                        VerifyRequest(
                                            otp = otp,
                                            email = email
                                        )
                                    )
                                }

                                "2" -> {
                                    authenticationViewModel.verifyResetPasswordOTP(
                                        VerifyRequest(
                                            otp = otp,
                                            email = email
                                        )
                                    )
                                }
                            }

                        } else {
                            openErrorDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),

                    ) {
                    Text(
                        "VERIFY",
                        modifier = Modifier.padding(horizontal = 40.dp, vertical = 5.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            
            Image(
                painter = painterResource(id = R.drawable.otp_background),
                contentDescription = "OTP Background"
            )

            Text(
                "OTP Verification",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "An 6-digits code has been sent to your email",
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )

            OTPTextField(
                otpLength = 6,
                modifier = Modifier.padding(horizontal = 24.dp),
                onOtpChanged = { otp = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "The OPT will be expired in ",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Didn't receive code?",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                TextButton(onClick = {
                    authenticationViewModel.forgotPasswordSendOTP(
                        Email(email)
                    )

                    Toast.makeText(
                        context,
                        "Check your email to receive a new OTP!",
                        Toast.LENGTH_SHORT
                    ).show()

                    timeInSeconds = expiredTime

                }) {
                    Text(text = "Resend")
                }
            }

        }
    }

    if (openErrorDialog) {
        AnimatedVisibility(
            visible = true,
            exit = scaleOut(),
            enter = scaleIn()
        ) {
            ErrorDialog(
                title = "Error",
                content = "Something went wrong. Press OK and try again!",
                showDialog = openErrorDialog,
                onDismiss = { openErrorDialog = false },
                onConfirm = { openErrorDialog = false }
            )
        }
    }
}

