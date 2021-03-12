package com.devswhocare.messenger.ui.message_detail.adapter

import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.databinding.CellFullChatMessageBinding
import com.devswhocare.messenger.ui.base.BaseViewHolder
import com.devswhocare.messenger.util.DateTimeUtils

class FullMessageHolder(
    private val binding: CellFullChatMessageBinding
): BaseViewHolder<Message>(binding) {

    fun setMessage(message: Message) {
        binding.messageTv.text = message.messageText
        binding.timeTv.text = DateTimeUtils.getTimeDateFromMillis(
            message.messagePostedTime.toLong()
        )
    }
}