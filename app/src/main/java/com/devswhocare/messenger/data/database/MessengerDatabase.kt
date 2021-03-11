package com.devswhocare.messenger.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devswhocare.messenger.data.database.dao.MessageDao
import com.devswhocare.messenger.data.model.Message

@Database(entities = [Message::class], version = 1)
abstract class MessengerDatabase: RoomDatabase() {

    abstract fun messagesDao(): MessageDao
}