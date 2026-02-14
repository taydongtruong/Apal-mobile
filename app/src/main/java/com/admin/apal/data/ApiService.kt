package com.admin.apal.data

import com.admin.apal.model.*
import retrofit2.http.*

interface ApiService {
    // Đăng nhập dùng Form-data như bạn viết trong auth.py
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") user: String,
        @Field("password") pass: String
    ): TokenResponse

    // Lấy danh sách chiến dịch đang chạy
    @GET("campaigns/active")
    suspend fun getActiveCampaigns(
        @Header("Authorization") token: String
    ): List<CampaignResponse>

    // Cập nhật FCM Token để đổ chuông
    @POST("auth/update-fcm-token")
    suspend fun updateFCMToken(
        @Header("Authorization") token: String,
        @Body fcmData: Map<String, String>
    ): Map<String, String>
}