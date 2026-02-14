package com.admin.apal.model

import com.google.gson.annotations.SerializedName

// 1. Phản hồi Login
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    val role: String,
    @SerializedName("user_id") val userId: Int
)

// 2. Thông tin User
data class UserResponse(
    val id: Int,
    val username: String,
    val role: String
)

// 3. Chiến dịch (Campaign)
data class CampaignResponse(
    val id: Int,
    val title: String,
    val description: String? = null, // Thêm cái này nếu muốn hiện mô tả
    @SerializedName("target_amount") val targetAmount: Long,
    @SerializedName("current_amount") val currentAmount: Long = 0, // Để biết đã góp được bao nhiêu
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String
)

// 4. Tin nhắn Chat
data class MessageResponse(
    val id: Int,
    val content: String,
    @SerializedName("sender_id") val senderId: Int,
    @SerializedName("receiver_id") val receiverId: Int,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String
)

// 5. Danh sách Inbox cho "Ông Chú"
data class ChatSummaryResponse(
    @SerializedName("user_id") val userId: Int,
    val username: String,
    @SerializedName("last_message") val lastMessage: String?,
    @SerializedName("last_time") val lastTime: String?,
    @SerializedName("unread_count") val unreadCount: Int
)