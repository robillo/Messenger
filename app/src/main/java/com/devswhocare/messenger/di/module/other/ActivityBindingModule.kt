package com.devswhocare.messenger.di.module.other

import com.devswhocare.messenger.di.module.activity.MessageDetailsModule
import com.devswhocare.messenger.di.module.activity.MessagesActivityModule
import com.devswhocare.messenger.ui.message_detail.MessageDetailActivity
import com.devswhocare.messenger.ui.messages.MessagesActivity
import com.devswhocare.productivitylauncher.di.scope.PerActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @PerActivityScope
    @ContributesAndroidInjector(modules = [MessagesActivityModule::class])
    internal abstract fun messagesActivity(): MessagesActivity

    @PerActivityScope
    @ContributesAndroidInjector(modules = [MessageDetailsModule::class])
    internal abstract fun messageDetailActivity(): MessageDetailActivity
}