package com.cricpro.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EmailOtpVerificationScreen(
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var otpCode by remember { mutableStateOf("") }
    var cooldownSeconds by remember { mutableStateOf(60) }
    val authState by viewModel.authState.collectAsState()

    val email = viewModel.activeOtpEmail

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onVerificationSuccess()
        }
    }

    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            kotlinx.coroutines.delay(1000L)
            cooldownSeconds--
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Verify Email OTP",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We have sent a 6-digit OTP code to:\n${email.ifBlank { "your email" }}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📩 6-Digit OTP Sent Successfully!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please check your email inbox and enter the OTP below.",
                        fontSize = 12.sp,
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) otpCode = it; if (authState is AuthState.Error) viewModel.resetState() },
                label = { Text("Enter 6-Digit Numeric OTP") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (authState is AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.verifyEmailOtp(otpCode) },
                    enabled = otpCode.length == 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Verify & Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        enabled = cooldownSeconds == 0,
                        onClick = {
                            cooldownSeconds = 60
                            viewModel.sendEmailOtp(email)
                        }
                    ) {
                        Text(
                            text = if (cooldownSeconds > 0) "Resend OTP (${cooldownSeconds}s)" else "Resend OTP Code",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(onClick = onNavigateBack) {
                        Text("Change Email")
                    }
                }
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
