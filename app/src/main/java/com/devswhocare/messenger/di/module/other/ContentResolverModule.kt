package com.devswhocare.messenger.di.module.other

import android.content.ContentResolver
import com.devswhocare.messenger.di.scope.MessengerAppScope
import dagger.Module
import dagger.Provides

@Module
class ContentResolverModule(private val contentResolver: ContentResolver) {

    @Provides
    @MessengerAppScope
    fun getContentResolver(): ContentResolver {
        return contentResolver
    }
}