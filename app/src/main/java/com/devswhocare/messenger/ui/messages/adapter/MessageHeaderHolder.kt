package com.devswhocare.messenger.ui.messages.adapter

import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.databinding.CellMessageHeaderBinding
import com.devswhocare.messenger.ui.base.BaseViewHolder

class MessageHeaderHolder(
    private val binding: CellMessageHeaderBinding
    ): BaseViewHolder<Message>(binding) {

        fun setHeader(header: String) {
            binding.hoursAgoTv.text = header
        }
}