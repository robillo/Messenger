package com.devswhocare.messenger.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.devswhocare.messenger.util.Constants.channelDescriptionNewMessageNotifications
import javax.inject.Inject

class NotificationUtil @Inject constructor(private val context: Context) {

    companion object {
        const val channelIdNewMessage = "new_message"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelIdNewMessage,
                channelDescriptionNewMessageNotifications,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}