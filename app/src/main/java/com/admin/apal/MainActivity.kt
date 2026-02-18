package com.admin.apal

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.NumberFormat
import java.util.*

// --- MODELS (Dữ liệu không đổi) ---
data class LoginResponse(val access_token: String, val role: String, val user_id: Int)
data class StatsResponse(val total_goal: Long, val current_total: Long, val pending_total: Long, val percentage: Double, val campaign_title: String)
data class UpdateFCMRequest(val fcm_token: String)

// --- API INTERFACE ---
interface ApiService {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(@Field("username") u: String, @Field("password") p: String): Response<LoginResponse>

    @GET("stats")
    suspend fun getStats(@Header("Authorization") token: String): Response<StatsResponse>

    @POST("auth/update-fcm-token")
    suspend fun updateFcm(@Header("Authorization") token: String, @Body req: UpdateFCMRequest): Response<Any>
}

// --- RETROFIT CLIENT ---
object RetrofitInstance {
    private const val BASE_URL = "https://apal-api.onrender.com/"
    val api: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Chủ đề màu đỏ Tết
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFFD32F2F), secondary = Color(0xFFFFC107))) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFFF8E1)) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("apal_prefs", Context.MODE_PRIVATE)

    var token by remember { mutableStateOf(sharedPrefs.getString("token", null)) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var stats by remember { mutableStateOf<StatsResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    if (token == null) {
        // --- MÀN HÌNH ĐĂNG NHẬP (Giao diện đỏ may mắn) ---
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🧧 APAL 2026 🧧", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))
            Text("Năm Bính Ngọ - Phát Tài Phát Lộc", fontSize = 14.sp, color = Color(0xFF8B0000))
            Spacer(Modifier.height(40.dp))
            OutlinedTextField(username, { username = it }, Modifier.fillMaxWidth(), label = { Text("Tên đăng nhập") }, shape = RoundedCornerShape(16.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Mật khẩu") }, visualTransformation = PasswordVisualTransformation(), shape = RoundedCornerShape(16.dp))
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    isLoading = true
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val res = RetrofitInstance.api.login(username, password)
                            if (res.isSuccessful) {
                                val body = res.body()
                                withContext(Dispatchers.Main) {
                                    token = body?.access_token
                                    sharedPrefs.edit().putString("token", token).apply()
                                    updateFcmOnServer(context, token!!)
                                }
                            } else {
                                withContext(Dispatchers.Main) { Toast.makeText(context, "Sai thông tin rồi bạn ơi! 🧨", Toast.LENGTH_SHORT).show() }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) { Toast.makeText(context, "Lỗi kết nối rồi!", Toast.LENGTH_SHORT).show() }
                        } finally { isLoading = false }
                    }
                },
                Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) { Text(if (isLoading) "ĐANG XỬ LÝ..." else "KHAI XUÂN ĐĂNG NHẬP", fontWeight = FontWeight.Bold) }
        }
    } else {
        // --- MÀN HÌNH DASHBOARD (Giao diện rực rỡ) ---
        LaunchedEffect(Unit) {
            try {
                val res = RetrofitInstance.api.getStats("Bearer $token")
                if (res.isSuccessful) stats = res.body()
            } catch (e: Exception) {}
        }

        Column(Modifier.fillMaxSize()) {
            // Header rực rỡ
            Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFFD32F2F), Color(0xFFFF5252)))).padding(20.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Chúc Mừng Năm Mới 2026", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Vạn sự như ý - Tỷ sự như mơ", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                    IconButton(onClick = { sharedPrefs.edit().clear().apply(); token = null }) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Column(Modifier.padding(20.dp)) {
                // Card mục tiêu
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFFFC107))
                            Spacer(Modifier.width(8.dp))
                            Text(stats?.campaign_title ?: "Mục tiêu năm mới", fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(currencyFormatter.format(stats?.current_total ?: 0), fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))
                        Text("Tiền đã về ví 🧧", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Thanh tiến độ xịn
                Text("Tiến độ hoàn thành: ${stats?.percentage ?: 0}%", fontWeight = FontWeight.Bold, color = Color(0xFFB71C1C))
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = (stats?.percentage?.toFloat() ?: 0f) / 100f,
                    modifier = Modifier.fillMaxWidth().height(16.dp).background(Color.White, RoundedCornerShape(8.dp)),
                    color = Color(0xFFFFC107), // Màu vàng tài lộc
                    trackColor = Color(0xFFEEEEEE)
                )

                Spacer(Modifier.weight(1f))

                // Dòng chữ chúc mừng cuối trang
                Text(
                    "🧧 Chúc bạn năm 2026 nạp tiền thật nhanh,\nlấy lại tất cả những gì đã mất! 🧧",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

fun updateFcmOnServer(context: Context, token: String) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val fcmToken = task.result
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitInstance.api.updateFcm("Bearer $token", UpdateFCMRequest(fcmToken))
                } catch (e: Exception) {}
            }
        }
    }
}