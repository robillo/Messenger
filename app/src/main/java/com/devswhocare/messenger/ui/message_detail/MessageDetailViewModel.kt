package com.devswhocare.messenger.ui.message_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.devswhocare.messenger.data.database.repository.MessageRepository
import com.devswhocare.messenger.data.model.Message
import javax.inject.Inject

class MessageDetailViewModel @Inject constructor(
    private val messageRepository: MessageRepository
): ViewModel() {

    fun getMessagesForAddress(address: String): LiveData<MutableList<Message>> {
        return messageRepository.getMessagesForAddress(address)
    }
}