package com.admin.apal.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.admin.apal.data.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Apal Login", fontSize = 32.sp, modifier = Modifier.padding(bottom = 32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Tên đăng nhập") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        scope.launch {
                            try {
                                // Gọi API Login
                                val response = RetrofitClient.instance.login(username, password)
                                isLoading = false
                                Toast.makeText(context, "Chào mừng ${response.role}!", Toast.LENGTH_SHORT).show()

                                // Trả về token và role để điều hướng
                                onLoginSuccess(response.accessToken, response.role)
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Lỗi: Sai tài khoản hoặc Server Render đang ngủ", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Đăng nhập")
            }
        }
    }
}