package com.devswhocare.messenger.di.module.other

import android.content.Context
import com.devswhocare.messenger.di.scope.MessengerAppScope
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    @MessengerAppScope
    fun context(): Context {
        return context
    }
}