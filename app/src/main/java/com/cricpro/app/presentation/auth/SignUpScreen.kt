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
import com.cricpro.app.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var step by remember { mutableStateOf(1) }
    var localErrorMsg by remember { mutableStateOf("") }
    
    // Step 1: Personal Info
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Step 2: Cricket Profile
    var primaryRole by remember { mutableStateOf("Batter") }
    var battingStyle by remember { mutableStateOf("Right-Hand Bat") }
    var bowlingStyle by remember { mutableStateOf("Right-arm Medium") }

    // Step 3: Location & PIN
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var securityPin by remember { mutableStateOf("") }
    var confirmSecurityPin by remember { mutableStateOf("") }
    var pinVisible by remember { mutableStateOf(false) }
    var confirmPinVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignUpSuccess()
        }
    }

    val roles = listOf("Batter", "Bowler", "All-Rounder", "Wicket-Keeper")
    val battingStyles = listOf("Right-Hand Bat", "Left-Hand Bat")
    val bowlingStyles = listOf(
        "Right-arm Fast",
        "Right-arm Medium",
        "Right-arm Off Spin",
        "Right-arm Leg Spin",
        "Left-arm Fast",
        "Left-arm Medium",
        "Left-arm Slow"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Player Onboarding",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Step $step of 3: ${if (step == 1) "Account Info" else if (step == 2) "Cricket Profile" else "Location & Security PIN"}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (step) {
                1 -> {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; localErrorMsg = ""; viewModel.resetState() },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; localErrorMsg = ""; viewModel.resetState() },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; localErrorMsg = ""; viewModel.resetState() },
                        label = { Text("Password (min 6 characters)") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "Hide" else "Show", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; localErrorMsg = ""; viewModel.resetState() },
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Text(if (confirmPasswordVisible) "Hide" else "Show", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                2 -> {
                    Text("Primary Playing Role", fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        roles.take(2).forEach { r ->
                            FilterChip(
                                selected = primaryRole == r,
                                onClick = { primaryRole = r },
                                label = { Text(r) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        roles.drop(2).forEach { r ->
                            FilterChip(
                                selected = primaryRole == r,
                                onClick = { primaryRole = r },
                                label = { Text(r) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Batting Style", fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        battingStyles.forEach { b ->
                            FilterChip(
                                selected = battingStyle == b,
                                onClick = { battingStyle = b },
                                label = { Text(b) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Bowling Style", fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        bowlingStyles.chunked(2).forEach { pair ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                pair.forEach { bs ->
                                    FilterChip(
                                        selected = bowlingStyle == bs,
                                        onClick = { bowlingStyle = bs },
                                        label = { Text(bs, fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                3 -> {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it; localErrorMsg = "" },
                        label = { Text("City") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it; localErrorMsg = "" },
                        label = { Text("State / Region") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = securityPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) { securityPin = it; localErrorMsg = "" } },
                        label = { Text("4-Digit Security PIN (For Instant Sign-In)") },
                        singleLine = true,
                        visualTransformation = if (pinVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { pinVisible = !pinVisible }) {
                                Text(if (pinVisible) "Hide" else "Show", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmSecurityPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) { confirmSecurityPin = it; localErrorMsg = "" } },
                        label = { Text("Confirm 4-Digit Security PIN") },
                        singleLine = true,
                        visualTransformation = if (confirmPinVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { confirmPinVisible = !confirmPinVisible }) {
                                Text(if (confirmPinVisible) "Hide" else "Show", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (authState is AuthState.Loading) {
                CircularProgressIndicator()
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step--; localErrorMsg = ""; viewModel.resetState() },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back", fontSize = 15.sp)
                        }
                    }

                    Button(
                        onClick = {
                            localErrorMsg = ""
                            if (step == 1) {
                                val trimmedName = fullName.trim()
                                val trimmedEmail = email.trim()
                                if (trimmedName.isBlank()) {
                                    localErrorMsg = "Please enter your Full Name"
                                    return@Button
                                }
                                if (trimmedEmail.isBlank() || !trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                                    localErrorMsg = "Please enter a valid email address (e.g. name@domain.com)"
                                    return@Button
                                }
                                if (password.length < 6) {
                                    localErrorMsg = "Password must be at least 6 characters"
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    localErrorMsg = "Passwords do not match"
                                    return@Button
                                }
                                step = 2
                            } else if (step == 2) {
                                step = 3
                            } else {
                                val trimmedPin = securityPin.trim()
                                val trimmedConfirmPin = confirmSecurityPin.trim()
                                if (trimmedPin.isNotBlank()) {
                                    if (trimmedPin.length != 4 || !trimmedPin.all { it.isDigit() }) {
                                        localErrorMsg = "Security PIN must be exactly 4 numeric digits"
                                        return@Button
                                    }
                                    if (trimmedPin != trimmedConfirmPin) {
                                        localErrorMsg = "Security PINs do not match"
                                        return@Button
                                    }
                                    if (trimmedPin == password.trim()) {
                                        localErrorMsg = "Security PIN should not be identical to your password"
                                        return@Button
                                    }
                                }
                                viewModel.signUp(
                                    name = fullName,
                                    email = email,
                                    pass = password,
                                    confirmPass = confirmPassword,
                                    pin = securityPin,
                                    confirmPin = confirmSecurityPin
                                )
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (step < 3) "Next" else "Complete Registration",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            if (localErrorMsg.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(localErrorMsg, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            } else if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text((authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?")
                TextButton(onClick = onNavigateToLogin) {
                    Text("Log In", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
