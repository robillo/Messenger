package com.devswhocare.messenger.ui.messages.adapter

import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.databinding.CellMessageBinding
import com.devswhocare.messenger.ui.base.BaseViewHolder
import com.devswhocare.messenger.util.ColorUtil

class MessageHolder(private val binding: CellMessageBinding): BaseViewHolder<Message>(binding) {

    private var avatarColor: Int = -1
    private lateinit var messageClickListener: MessageClickListener

    fun setMessage(message: Message, position: Int) {
        binding.messageSenderNameTv.text = message.messageSenderAddress
        binding.messageSenderMessageTv.text = message.messageText

        val initialLetter = message.messageSenderAddress.first()
        if(Character.isLetter(initialLetter)) {
            binding.messageSenderInitialTv.visibility = View.VISIBLE
            binding.personIv.visibility = View.GONE
            binding.messageSenderInitialTv.text = initialLetter.toString()
        }
        else {
            binding.messageSenderInitialTv.visibility = View.GONE
            binding.personIv.visibility = View.VISIBLE
        }

        avatarColor = ColorUtil.getAvatarColorForPosition(position)

        binding.profileBackgroundIv.setColorFilter(
            ContextCompat.getColor(binding.root.context, avatarColor),
            PorterDuff.Mode.SRC_ATOP
        )

        binding.root.setOnClickListener {
            if(::messageClickListener.isInitialized)
                messageClickListener.onMessageClicked(
                    position, message, avatarColor
                )
        }
    }

    fun setMessageClickListener(listener: MessageClickListener) {
        this.messageClickListener = listener
    }

    interface MessageClickListener {
        fun onMessageClicked(position: Int, message: Message, accentColor: Int)
    }
}