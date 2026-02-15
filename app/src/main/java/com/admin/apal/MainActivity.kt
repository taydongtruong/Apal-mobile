package com.admin.apal

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.admin.apal.data.SharedPrefs
import com.admin.apal.ui.LoginScreen
import com.admin.apal.ui.UserDashboard
import com.admin.apal.ui.theme.ApalTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Bạn sẽ không nhận được thông báo nạp tiền!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        saveAndCopyToken()

        setContent {
            ApalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    // State quản lý việc đăng nhập
                    var isLoggedIn by remember {
                        mutableStateOf(SharedPrefs.getToken(context) != null)
                    }

                    if (!isLoggedIn) {
                        // Màn hình Login
                        LoginScreen { token, role ->
                            SharedPrefs.saveAuth(context, token, role)
                            isLoggedIn = true
                        }
                    } else {
                        // Màn hình Dashboard sau khi đăng nhập thành công
                        UserDashboard(onLogout = {
                            isLoggedIn = false
                        })
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun saveAndCopyToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                SharedPrefs.saveFCMToken(this, token)
                copyToClipboard(token)
            }
        }
    }

    private fun copyToClipboard(text: String?) {
        if (text == null) return
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("FCM Token", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Đã tự động copy Token!", Toast.LENGTH_SHORT).show()
    }
}