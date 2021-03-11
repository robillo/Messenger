package com.devswhocare.messenger.di.component

import android.content.ContentResolver
import android.content.Context
import com.devswhocare.messenger.MessengerApp
import com.devswhocare.messenger.di.module.other.*
import com.devswhocare.messenger.di.scope.MessengerAppScope
import com.devswhocare.messenger.util.MessagesUtil
import com.devswhocare.messenger.util.NotificationUtil
import com.devswhocare.messenger.util.SharedPreferenceUtil
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.inflationx.calligraphy3.CalligraphyInterceptor

@MessengerAppScope
@Component(
    modules = [
        ActivityBindingModule::class,
        AndroidSupportInjectionModule::class,
        SharedPreferenceModule::class,
        ViewModelFactoryModule::class,
        MessengerAppModule::class,
        MessagesUtilsModule::class,
        RoomRepositoryModule::class
    ]
)
interface MessengerAppComponent: AndroidInjector<MessengerApp> {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance contentResolver: ContentResolver,
            @BindsInstance applicationContext: Context
        ): MessengerAppComponent
    }

    fun context(): Context

    fun getSharedPreferenceUtil(): SharedPreferenceUtil

    fun getNotificationUtil(): NotificationUtil

    fun calligraphyInterceptor(): CalligraphyInterceptor

    fun getMessagesUtil(): MessagesUtil
}