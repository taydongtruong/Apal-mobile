package com.admin.apal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.admin.apal.MainActivity
import com.admin.apal.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 1. Log để kiểm tra xem data có về không (Xem trong Logcat)
        Log.d("FCM_RECEIVE", "Data: ${remoteMessage.data}")

        val title = remoteMessage.data["title"] ?: "APAL Finance"
        val body = remoteMessage.data["body"] ?: "Bạn có thông báo mới"
        val isRinging = remoteMessage.data["is_ringing"] == "true"
        val type = remoteMessage.data["type"] ?: "default"

        // 2. Xử lý Rung và Chuông
        if (type == "ai_lover") {
            vibrateHeartbeat()
        } else if (isRinging) {
            triggerRingingEffect()
        }

        // 3. Hiển thị thông báo
        sendNotification(title, body, isRinging)
    }

    private fun vibrateHeartbeat() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 150, 100, 150)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION") vibrator.vibrate(pattern, -1)
        }
    }

    private fun triggerRingingEffect() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION") vibrator.vibrate(1000)
        }
    }

    private fun sendNotification(title: String, body: String, isRinging: Boolean) {
        val channelId = "apal_urgent_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Âm thanh thông báo mặc định
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 4. TẠO CHANNEL (Cực kỳ quan trọng để hiện thông báo nổi)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Apal Urgent Notifications",
                NotificationManager.IMPORTANCE_HIGH // Buộc phải là HIGH để hiện biểu ngữ
            ).apply {
                description = "Kênh thông báo quan trọng của Apal"
                enableLights(true)
                enableVibration(true)
                // Thiết lập âm thanh cho Channel (Android 8+ thiết lập ở đây)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 5. BUILD NOTIFICATION
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            // Nếu ic_verified bị lỗi, hãy tạm thay bằng android.R.drawable.stat_notify_chat
            .setSmallIcon(R.drawable.ic_verified)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Ưu tiên tối đa
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Hiện nội dung cả khi khóa máy
            .setContentIntent(pendingIntent)

        // Nếu là tin nhắn "đổ chuông", làm cho nó dai dẳng hơn
        if (isRinging) {
            notificationBuilder.setOngoing(false) // Không bắt buộc nhưng tăng sự chú ý
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Token mới: $token")
    }
}