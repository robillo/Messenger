package com.devswhocare.messenger.util

import android.content.ContentResolver
import android.provider.Telephony
import androidx.annotation.WorkerThread
import com.devswhocare.messenger.data.model.Message

class MessagesUtil(private val contentResolver: ContentResolver) {

    private var messageCount = 0
    private lateinit var message: Message
    private lateinit var messagesList: ArrayList<Message>

    @WorkerThread
    suspend fun getAllSmsFromProvider(): MutableList<Message> {
        val lstSms: MutableList<Message> = ArrayList()
        val cr: ContentResolver = contentResolver
        cr.query(
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
                    val time = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    message = Message(
                        messageId = cursor.getString(cursor.getColumnIndexOrThrow("_id")),
                        messageSenderAddress = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                        messageText = cursor.getString(cursor.getColumnIndexOrThrow("body")),
                        messageReadState = cursor.getString(cursor.getColumnIndex("read")),
                        messagePostedTime = time,
                        folderName = if (cursor.getString(cursor.getColumnIndexOrThrow("type")).contains(
                                Constants.FLAG_TYPE_INBOX
                            )
                        ) {
                            Constants.FOLDER_TYPE_INBOX
                        } else {
                            Constants.FOLDER_TYPE_SENT
                        }
                    )
//                    hoursAgo = DateTimeUtils.getHoursAgoAccordingToCurrentTimeUsingMillis(time.toLong())
                    lstSms.add(message)
                    cursor.moveToNext()
                }
            }
            cursor.close()
        }
        return lstSms
    }

//    private suspend fun getGroupDataIntoHashMap(): HashMap<Long, ArrayList<Message>> {
//        val map: HashMap<Long, ArrayList<Message>> = HashMap()
//        getAllSmsFromProvider().forEach {
//            val hoursAgo = it.hoursAgo
//            if (map.containsKey(hoursAgo)) {
//                map[hoursAgo]?.add(it)
//            } else {
//                val list = ArrayList<Message>()
//                list.add(it)
//                map[hoursAgo] = list
//            }
//        }
//        return map
//    }
//
//    @WorkerThread
//    suspend fun getConsolidatedList(): ArrayList<Message> {
//        messagesList = ArrayList(messageCount + 1)
//        getGroupDataIntoHashMap().let { map ->
//            map.keys.forEach {
//                val headerItem = HeaderItem(it)
//                messagesList.add(headerItem)
//                map[it]?.forEach {
//                    val dataItem = DataItem(it)
//                    messagesList.add(dataItem)
//                }
//            }
//        }
//        return messagesList
//    }
}