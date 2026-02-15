package com.admin.apal.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.admin.apal.data.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoginView by remember { mutableStateOf(true) }
    var role by remember { mutableStateOf("nephew") } // Default role

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkTheme) {
                    // Trong Dark Mode, dùng màu phẳng đơn giản
                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background))
                } else {
                    // Trong Light Mode, giữ nguyên Gradient của bạn
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFE0F7FA), Color.White, Color(0xFFF1F5F9)),
                        center = Offset(0f, 0f),
                        radius = 2500f
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(56.dp), // Corresponds to rounded-[3.5rem]
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface, // It will be semi-transparent in dark mode from Theme.kt
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp),
                    tonalElevation = 8.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Shield Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(12.dp).size(36.dp)
                    )
                }

                Text(
                    text = "APAL FINANCE",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = if (isLoginView) "Chào mừng bạn quay trở lại!" else "Bắt đầu quản lý tài chính ngay",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên đăng nhập") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(image, "Toggle password visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                if (!isLoginView) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Bạn là ai?",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Button(onClick = { role = "nephew" }, modifier = Modifier.weight(1f), enabled = role != "nephew", shape = RoundedCornerShape(16.dp) ) {
                            Text("Người góp")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { role = "uncle" }, modifier = Modifier.weight(1f), enabled = role != "uncle", shape = RoundedCornerShape(16.dp) ) {
                            Text("Ông Chủ")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        onClick = {
                            if (username.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true
                                scope.launch {
                                    try {
                                        if (isLoginView) {
                                            val response = RetrofitClient.instance.login(username, password)
                                            onLoginSuccess(response.accessToken, response.role)
                                            Toast.makeText(context, "Chào mừng ${response.role}!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            val response = RetrofitClient.instance.register(username, password, role)
                                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                            isLoginView = true
                                        }
                                    } catch (e: Exception) {
                                        val errorMessage = e.message ?: "Đã có lỗi xảy ra"
                                        Toast.makeText(context, "Lỗi: $errorMessage", Toast.LENGTH_LONG).show()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text(if (isLoginView) "ĐĂNG NHẬP" else "TẠO TÀI KHOẢN")
                    }
                }

                TextButton(onClick = { isLoginView = !isLoginView }, modifier = Modifier.padding(top = 16.dp)) {
                    Text(if (isLoginView) "Chưa có tài khoản? Đăng ký ngay" else "Đã có tài khoản? Đăng nhập")
                }
            }
        }
    }
}

