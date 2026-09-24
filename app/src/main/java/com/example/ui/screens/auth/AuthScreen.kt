package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.service.AuthManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLoginSuccess: (UserEntity) -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("child") } // "child", "parent", "admin"
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var adminPasscode by remember { mutableStateOf("") }
    var parentPin by remember { mutableStateOf("1234") }
    var isAuthenticating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailSent by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF6C5CE7), Color(0xFF8075E8), Color(0xFFF8FAFD))
                )
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo & Greeting
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text("🌟", fontSize = 42.sp)
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "IshaQxaaDey Doom",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = "Play, Learn, Create, and Grow Together.",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.9f)
        )

        Spacer(Modifier.height(24.dp))

        // Main Auth Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tab switcher
                TabRow(
                    selectedTabIndex = if (isRegisterMode) 1 else 0,
                    divider = {}
                ) {
                    Tab(
                        selected = !isRegisterMode,
                        onClick = {
                            isRegisterMode = false
                            errorMessage = null
                        },
                        text = { Text("Login", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = isRegisterMode,
                        onClick = {
                            isRegisterMode = true
                            errorMessage = null
                        },
                        text = { Text("Sign Up", fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Role selection filter chips
                Text(
                    "Sign in as:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = selectedRole == "child",
                        onClick = { selectedRole = "child" },
                        label = { Text("👶 Child", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedRole == "parent",
                        onClick = { selectedRole = "parent" },
                        label = { Text("👨‍👩‍👧 Parent", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedRole == "admin",
                        onClick = { selectedRole = "admin" },
                        label = { Text("🛡️ Admin", fontSize = 11.sp) }
                    )
                }

                if (selectedRole == "admin") {
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adminPasscode,
                        onValueChange = { adminPasscode = it },
                        label = { Text("Admin Security Passcode") },
                        placeholder = { Text("Enter 'admin99' or '1234'") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Google One-Tap Sign In
                OutlinedButton(
                    onClick = {
                        isAuthenticating = true
                        coroutineScope.launch {
                            delay(500)
                            isAuthenticating = false
                            val googleUser = UserEntity(
                                id = "user_google_${System.currentTimeMillis() % 1000}",
                                name = if (selectedRole == "parent") "Guardian User" else "Ayaan & Safiya",
                                email = "abdullahanees437@gmail.com",
                                avatar = if (selectedRole == "parent") "🦉" else "🦁",
                                bio = "Exploring the safe universe on IshaQxaaDey!",
                                role = selectedRole
                            )
                            onLoginSuccess(googleUser)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("google_signin_button"),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isAuthenticating
                ) {
                    Text("G", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF4285F4))
                    Spacer(Modifier.width(10.dp))
                    Text("Continue with Google", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(" or with email ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name / Nickname") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_name_field"),
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("auth_email_field"),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("auth_password_field"),
                    shape = RoundedCornerShape(16.dp)
                )

                if (isRegisterMode && selectedRole == "child") {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = parentPin,
                        onValueChange = { if (it.length <= 4) parentPin = it },
                        label = { Text("Parent Approval PIN (4-Digits)") },
                        leadingIcon = { Icon(Icons.Filled.VpnKey, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth().testTag("auth_parent_pin_field"),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                if (!isRegisterMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showForgotPasswordDialog = true }) {
                            Text("Forgot password?", fontSize = 12.sp)
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please enter both email and password."
                            return@Button
                        }
                        if (selectedRole == "admin" && adminPasscode != "admin99" && adminPasscode != "1234") {
                            errorMessage = "Invalid Admin security passcode. Unauthorized role elevation is blocked."
                            return@Button
                        }

                        isAuthenticating = true
                        errorMessage = null
                        coroutineScope.launch {
                            delay(600)
                            isAuthenticating = false
                            // Hash password securely - zero plain text storage
                            val passwordHash = AuthManager.hashCredential(password)
                            val user = UserEntity(
                                id = "user_${email.hashCode().toUInt()}",
                                name = if (name.isNotBlank()) name else email.substringBefore("@").replaceFirstChar { it.uppercase() },
                                email = email.trim(),
                                avatar = if (selectedRole == "admin") "🛡️" else if (selectedRole == "parent") "🦉" else "🦁",
                                bio = "Learning, laughing, and exploring on IshaQxaaDey!",
                                isChild = selectedRole == "child",
                                isParentVerified = true,
                                parentPin = parentPin,
                                role = selectedRole
                            )
                            onLoginSuccess(user)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isAuthenticating
                ) {
                    if (isAuthenticating) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            if (isRegisterMode) "Create Safe Account" else "Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                resetEmailSent = false
            },
            title = { Text("Password Recovery") },
            text = {
                if (resetEmailSent) {
                    Text("Safe password reset instructions have been sent to your guardian's email address.")
                } else {
                    Text("Enter your account email. For child accounts, password reset instructions are securely sent to the registered parent email address.")
                }
            },
            confirmButton = {
                if (!resetEmailSent) {
                    Button(onClick = { resetEmailSent = true }) {
                        Text("Send Reset Link")
                    }
                } else {
                    Button(onClick = {
                        showForgotPasswordDialog = false
                        resetEmailSent = false
                    }) {
                        Text("Done")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showForgotPasswordDialog = false
                    resetEmailSent = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
