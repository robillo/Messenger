package com.devswhocare.messenger.di.module.other

import android.content.Context
import androidx.room.Room
import com.devswhocare.messenger.data.database.MessengerDatabase
import com.devswhocare.messenger.data.database.dao.MessageDao
import com.devswhocare.messenger.data.database.repository.MessageRepository
import com.devswhocare.messenger.di.scope.MessengerAppScope
import dagger.Module
import dagger.Provides

@Module
class RoomRepositoryModule {

    @Provides
    @MessengerAppScope
    fun messengerAppDatabase(context: Context): MessengerDatabase {
        return Room.databaseBuilder(
            context,
            MessengerDatabase::class.java,
            "messenger_app_database"
        ).build()
    }

    @Provides
    @MessengerAppScope
    fun appDao(messageDatabase: MessengerDatabase): MessageDao {
        return messageDatabase.messagesDao()
    }

    @Provides
    @MessengerAppScope
    fun roomRepository(messageDao: MessageDao): MessageRepository {
        return MessageRepository(messageDao)
    }
}