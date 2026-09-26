package com.cricpro.app.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToEmailOtp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Password, 1: Email OTP, 2: Security PIN
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var securityPin by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var pinVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        } else if (authState is AuthState.OtpSent) {
            onNavigateToEmailOtp()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("CricPro", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Cricket Scoring & Tournament Platform", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Google Sign-In Button
            Button(
                onClick = {
                    val dummyName = "Google Player"
                    val googleEmail = email.trim()
                    viewModel.loginWithGoogle(dummyName, googleEmail, "")
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("🚀 1-Tap Google Sign-In (Free)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tabs for Password, Free Email OTP, and Security PIN
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0; viewModel.resetState() }, text = { Text("Password") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; viewModel.resetState() }, text = { Text("Email OTP") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2; viewModel.resetState() }, text = { Text("Security PIN") })
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; if (authState is AuthState.Error) viewModel.resetState() },
                label = { Text("Email Address (e.g. player@domain.com)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                0 -> {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; if (authState is AuthState.Error) viewModel.resetState() },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "Hide" else "Show", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = onNavigateToForgotPassword,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot Password?")
                    }
                }
                1 -> {
                    Text(
                        text = "We will generate and send a 6-digit OTP code to your email for free.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                2 -> {
                    OutlinedTextField(
                        value = securityPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) securityPin = it; if (authState is AuthState.Error) viewModel.resetState() },
                        label = { Text("4-Digit Security PIN") },
                        singleLine = true,
                        visualTransformation = if (pinVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { pinVisible = !pinVisible }) {
                                Text(if (pinVisible) "Hide" else "Show", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (authState is AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        when (selectedTab) {
                            0 -> viewModel.login(email, password)
                            1 -> viewModel.sendEmailOtp(email)
                            2 -> viewModel.loginWithPin(email, securityPin)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (selectedTab) {
                            0 -> "Login with Password"
                            1 -> "Send Free Email OTP"
                            else -> "Sign In with PIN"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { viewModel.continueAsGuest() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue as Guest (Browse Only)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?")
                TextButton(onClick = onNavigateToSignUp) {
                    Text("Sign Up", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
