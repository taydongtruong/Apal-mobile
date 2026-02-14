package com.admin.apal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.admin.apal.data.SharedPrefs
import com.admin.apal.ui.LoginScreen
import com.admin.apal.ui.UserDashboard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // MaterialTheme giúp giao diện có màu sắc và font chữ chuẩn
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    // Khai báo state để Compose biết khi nào cần vẽ lại màn hình
                    var isLoggedIn by remember { mutableStateOf(SharedPrefs.getToken(context) != null) }
                    var userRole by remember { mutableStateOf(SharedPrefs.getRole(context) ?: "") }

                    if (!isLoggedIn) {
                        LoginScreen { token, role ->
                            SharedPrefs.saveAuth(context, token, role)
                            userRole = role
                            isLoggedIn = true
                        }
                    } else {
                        // Màn hình tạm thời sau khi đăng nhập thành công
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (userRole == "uncle") {
                                Text("Chào mừng Ông Chú (Admin)!", style = MaterialTheme.typography.headlineMedium)
                            } else {
                                UserDashboard()
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(onClick = {
                                SharedPrefs.clear(context)
                                isLoggedIn = false
                            }) {
                                Text("Đăng xuất")
                            }
                        }
                    }
                }
            }
        }
    }
}