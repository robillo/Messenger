package com.devswhocare.messenger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
   @PrimaryKey
   val messageId: String,
   val messageSenderAddress: String,
   val messageText: String,
   val messageReadState: String,
   val messagePostedTime: String,
   val folderName: String,
   var messagePostedHour: String? = "",
   val threadId: String
)