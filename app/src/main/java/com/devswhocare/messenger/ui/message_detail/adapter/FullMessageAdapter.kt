package com.devswhocare.messenger.ui.message_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.databinding.CellFullChatMessageBinding

class FullMessageAdapter: RecyclerView.Adapter<FullMessageHolder>() {

    private lateinit var messageList: List<Message>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullMessageHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FullMessageHolder(
            CellFullChatMessageBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FullMessageHolder, position: Int) {
        holder.setMessage(messageList[position])
    }

    override fun getItemCount(): Int {
        return if(::messageList.isInitialized) messageList.size
        else 0
    }

    fun setMessages(messageList: List<Message>) {
        this.messageList = messageList
        notifyDataSetChanged()
    }
}