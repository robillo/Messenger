package com.devswhocare.messenger.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devswhocare.messenger.data.model.Message

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessages(messageList: MutableList<Message>)

    @Query("SELECT * FROM messages ORDER BY messagePostedTime DESC LIMIT :limit OFFSET :offset")
    fun getMessagesForPage(offset: Int, limit: Int): LiveData<MutableList<Message>>

    @Query("SELECT * FROM messages WHERE messageSenderAddress = :address ORDER BY messagePostedTime DESC")
    fun getMessagesForAddress(address: String): LiveData<MutableList<Message>>
}