package com.devswhocare.messenger.ui.messages.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.data.model.MessageListItem
import com.devswhocare.messenger.databinding.CellMessageBinding
import com.devswhocare.messenger.databinding.CellMessageHeaderBinding

class MessageAdapter(
    comparator: DiffUtil.ItemCallback<Message>
): ListAdapter<Message, RecyclerView.ViewHolder>(comparator), MessageHolder.MessageClickListener {

    companion object {
        private const val typeTimeAgo = 0
        private const val typeMessage = 1
    }

    private lateinit var messageClickListener: MessageClickListener

    private var messagesList: MutableList<MessageListItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if(viewType == typeTimeAgo) {
            MessageHeaderHolder(
                CellMessageHeaderBinding.inflate(layoutInflater, parent, false)
            )
        }
        else {
            MessageHolder(
                CellMessageBinding.inflate(layoutInflater, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return messagesList[position].header?.let {
            typeTimeAgo
        } ?: run {
            typeMessage
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is MessageHolder -> {
                holder.setMessage(messagesList[position].message!!, position)
                holder.setMessageClickListener(this)
            }
            is MessageHeaderHolder -> {
                holder.setHeader(messagesList[position].header!!)
            }
        }
    }

    fun setMessagesList(messageItemsList: MutableList<MessageListItem>) {
        this.messagesList = messageItemsList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    fun addMessages(addedMessages: List<MessageListItem>) {
        val lastMessageListSize = messagesList.size
        messagesList.addAll(lastMessageListSize, addedMessages)
        notifyItemRangeInserted(lastMessageListSize, addedMessages.size)
    }

    override fun onMessageClicked(position: Int, message: Message, accentColor: Int) {
        if(::messageClickListener.isInitialized)
            messageClickListener.onMessageClicked(position, message, accentColor)
    }

    fun setMessageClickListener(listener: MessageClickListener) {
        this.messageClickListener = listener
    }

    interface MessageClickListener {
        fun onMessageClicked(position: Int, message: Message, accentColor: Int)
    }
}