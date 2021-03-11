package com.devswhocare.messenger.di.module.other

import android.content.ContentResolver
import com.devswhocare.messenger.di.scope.MessengerAppScope
import com.devswhocare.messenger.util.MessagesUtil
import dagger.Module
import dagger.Provides

@Module
class MessagesUtilsModule {

    @Provides
    @MessengerAppScope
    fun getMessagesUtil(contentResolver: ContentResolver): MessagesUtil {
        return MessagesUtil(contentResolver)
    }
}