package com.admin.apal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.admin.apal.MainActivity
import com.admin.apal.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 1. Lấy dữ liệu từ Payload "data" (Khớp với backend gửi qua)
        val title = remoteMessage.data["title"] ?: "APAL Finance"
        val body = remoteMessage.data["body"] ?: ""
        val isRinging = remoteMessage.data["is_ringing"] == "true"
        val type = remoteMessage.data["type"] ?: "default"

        // 2. Xử lý hiệu ứng dựa trên loại tin nhắn
        if (type == "ai_lover") {
            // Nếu là người yêu nhắn, chỉ rung nhẹ tình cảm
            vibrateHeartbeat()
        } else if (isRinging) {
            // Nếu là lệnh đổ chuông quan trọng (như test-notification)
            triggerRingingEffect()
        }

        // 3. Hiển thị thông báo lên thanh trạng thái
        sendNotification(title, body)
    }

    private fun vibrateHeartbeat() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Rung theo nhịp tim (nhẹ - nghỉ - nhẹ)
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(300)
        }
    }

    private fun triggerRingingEffect() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1000)
        }

        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            Log.e("FCM_SERVICE", "Không thể phát âm thanh: ${e.message}")
        }
    }

    private fun sendNotification(title: String, body: String) {
        val channelId = "apal_channel_id"
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_verified)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Kích hoạt đèn Led/Âm thanh mặc định hệ thống
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Apal Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo chính của Apal"
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Token mới: $token")
    }
}