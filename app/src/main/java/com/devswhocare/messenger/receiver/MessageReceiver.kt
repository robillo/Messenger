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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devswhocare.messenger.R
import com.devswhocare.messenger.ui.messages.MessagesActivity

class MessageReceiver: BroadcastReceiver() {

    companion object {
        private const val intentActionMessageReceived =
                "android.provider.Telephony.SMS_RECEIVED"
        private const val CHANNEL_ID = "1234"
        private const val CHANNEL_NAME = "ReceiveSMSChannel"
        private const val CHANNEL_DESCRIPTION = "ReceiveSMSDescription"
        private const val KEY_FORMAT = "format"
        private const val notificationIdMessage = 12
        private const val requestCodeMessagesActivity = 10
        private const val flagsMessagesActivity = PendingIntent.FLAG_UPDATE_CURRENT
        private const val pduString = "pdus"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { it ->
            if (it.action.equals(intentActionMessageReceived)) {
                it.extras?.let { bundle ->
                    val pduObjects =
                        bundle[pduString] as Array<Any>?
                    if (pduObjects != null) {
                        for (aObject in pduObjects) {
                            Log.e("mytag", "$aObject ${getIncomingMessage(aObject, bundle)}")
                            getIncomingMessage(aObject, bundle)?.let { currentSMS ->
                                Log.e("mytag", "${currentSMS.displayOriginatingAddress}")
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
        Log.e("mytag", "notification sender number $senderNo")
        val intent = MessagesActivity.newIntent(context, senderNo)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                requestCodeMessagesActivity,
                intent,
                flagsMessagesActivity
        )
        with(NotificationManagerCompat.from(context)) {
            notify(
                    notificationIdMessage,
                    getNotificationBuilder(
                            context,
                            senderNo,
                            message,
                            pendingIntent
                    ).build()
            )
        }
    }

    private fun getNotificationBuilder(
            context: Context,
            contentTitle: String,
            contentMessage: String,
            pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(contentTitle)
                .setContentText(contentMessage)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
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