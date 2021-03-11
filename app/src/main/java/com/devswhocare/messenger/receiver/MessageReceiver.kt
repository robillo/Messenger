package com.devswhocare.messenger.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devswhocare.messenger.R
import com.devswhocare.messenger.ui.messages.MessagesActivity

class MessageReceiver: BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "1234"
        private const val CHANNEL_NAME = "ReceiveSMSChannel"
        private const val CHANNEL_DESCRIPTION = "ReceiveSMSDescription"
        private const val KEY_FORMAT = "format"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { it ->
            if (it.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
                it.extras?.let { bundle ->
                    val pdu_Objects =
                        bundle["pdus"] as Array<Any>?
                    if (pdu_Objects != null) {
                        for (aObject in pdu_Objects) {
                            getIncomingMessage(aObject, bundle)?.let { currentSMS ->
                                val senderNo: String = currentSMS.displayOriginatingAddress
                                val message: String = currentSMS.displayMessageBody
                                issueNotification(context!!, senderNo, message)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun issueNotification(
        context: Context,
        senderNo: String,
        message: String
    ) {
        createNotificationChannel(context)
        val intent = MessagesActivity.newIntent(context, senderNo)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(senderNo)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(12, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    @Suppress("DEPRECATION")
    private fun getIncomingMessage(
        someObject: Any,
        bundle: Bundle
    ): SmsMessage? {
        val currentSMS: SmsMessage
        currentSMS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format = bundle.getString(KEY_FORMAT)
            SmsMessage.createFromPdu(someObject as ByteArray, format)
        }
        else {
            SmsMessage.createFromPdu(someObject as ByteArray)
        }
        return currentSMS
    }
}