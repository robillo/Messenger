package com.devswhocare.messenger.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.provider.Telephony
import android.util.Log
import androidx.annotation.WorkerThread
import com.devswhocare.messenger.data.model.Message
import java.lang.Exception

class MessagesUtil(private val contentResolver: ContentResolver) {

    private var messageCount = 0
    private lateinit var message: Message

    companion object {
        const val columnNameDate = "date"
        const val columnNameId = "_id"
        const val columnNameAddress = "address"
        const val columnNameBody = "body"
        const val columnNameRead = "read"
        const val columnNameType = "type"
        const val emptyString = ""
    }

    @SuppressLint("Recycle")
    @WorkerThread
    suspend fun getAllSmsFromProvider(): MutableList<Message> {
        val messagesList: MutableList<Message> = ArrayList()
        contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.READ,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE
            ),
            null,
            null,
            Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )?.let { cursor ->
            messageCount = cursor.count
            if (cursor.moveToFirst()) {
                for (i in 0 until messageCount) {
                    val time = getStringForColumn(cursor, columnNameDate)
                    message = Message(
                        messageId = getStringForColumn(cursor, columnNameId),
                        messageSenderAddress = getStringForColumn(cursor, columnNameAddress),
                        messageText = getStringForColumn(cursor, columnNameBody),
                        messageReadState = getStringForColumn(cursor, columnNameRead),
                        messagePostedTime = time,
                        folderName = getFolderName(cursor)
                    )
                    messagesList.add(message)
                    cursor.moveToNext()
                }
            }
            cursor.close()
        }
        return messagesList
    }

    private fun getStringForColumn(cursor: Cursor, columnName: String): String {
        return try {
            cursor.getString(cursor.getColumnIndex(columnName))
        }
        catch (ignored: Exception) {
            emptyString
        }
    }

    private fun getFolderName(cursor: Cursor): String {
        return if (cursor.getString(cursor.getColumnIndex(columnNameType)).contains(Constants.FLAG_TYPE_INBOX))
            Constants.FOLDER_TYPE_INBOX
        else
            Constants.FOLDER_TYPE_SENT
    }
}