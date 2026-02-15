package com.admin.apal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.admin.apal.data.SharedPrefs
import androidx.compose.ui.platform.LocalContext

@Composable
fun UserDashboard(onLogout: () -> Unit) {
    val context = LocalContext.current
    // Lấy tên từ bộ nhớ nếu có, không thì mặc định là "anh chàng đẹp trai"
    val username =  "Reak Smaay là anh chàng đẹp trai nhất làng"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dòng chào đơn giản, thân thiện
        Text(
            text = "Chào $username! 👋",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hôm nay bạn trông thật phong độ.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Nút đăng xuất để quay về trang Login
        Button(
            onClick = {
                SharedPrefs.clear(context) // Xóa sạch token/data
                onLogout() // Gọi hàm để MainActivity chuyển về LoginScreen
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("TRỞ VỀ TRANG ĐĂNG NHẬP", fontWeight = FontWeight.Black)
        }
    }
}