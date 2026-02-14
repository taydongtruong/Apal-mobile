package com.admin.apal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.admin.apal.data.RetrofitClient
import com.admin.apal.data.SharedPrefs
import com.admin.apal.model.CampaignResponse
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var campaigns by remember { mutableStateOf<List<CampaignResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Gọi API lấy danh sách chiến dịch khi mở màn hình
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val token = "Bearer ${SharedPrefs.getToken(context)}"
                campaigns = RetrofitClient.instance.getActiveCampaigns(token)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Chiến Dịch Góp Vốn") }) }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(campaigns) { campaign ->
                    CampaignItem(campaign)
                }
            }
        }
    }
}

@Composable
fun CampaignItem(campaign: CampaignResponse) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = campaign.title, style = MaterialTheme.typography.titleLarge)
            Text(text = "Mục tiêu: ${campaign.targetAmount} VNĐ", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* Sẽ viết logic gửi ảnh ở đây */ }) {
                Text("Gửi bằng chứng góp vốn")
            }
        }
    }
}