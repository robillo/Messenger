package com.devswhocare.messenger.data.database.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.devswhocare.messenger.data.database.dao.MessageDao
import com.devswhocare.messenger.data.model.Message

class MessageRepository(private val messageDao: MessageDao) {

    companion object {
        private val pageSize = 10
        private val indexOffset = 1
    }

    fun storeMessagesLocally(messageList: MutableList<Message>) {
        messageDao.insertMessages(messageList)
    }

    fun getMessagesForPage(pageNumber: Int): LiveData<MutableList<Message>> {
        Log.e("mytag", "offset ${(pageNumber - 1) * indexOffset} $pageSize")
        return messageDao.getMessagesForPage(
            (pageNumber - indexOffset) * pageSize,
            pageSize
        )
    }

    fun getMessagesForAddress(address: String): LiveData<MutableList<Message>> {
        return messageDao.getMessagesForAddress(address)
    }
}