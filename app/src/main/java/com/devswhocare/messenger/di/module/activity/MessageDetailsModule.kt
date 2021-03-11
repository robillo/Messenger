package com.devswhocare.messenger.di.module.activity

import androidx.lifecycle.ViewModel
import com.devswhocare.messenger.di.mapkey.ViewModelKey
import com.devswhocare.messenger.ui.message_detail.MessageDetailViewModel
import com.devswhocare.messenger.ui.message_detail.adapter.FullMessageAdapter
import com.devswhocare.productivitylauncher.di.scope.PerActivityScope
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class MessageDetailsModule {

    companion object {
        @Provides
        @PerActivityScope
        fun adapter(): FullMessageAdapter {
            return FullMessageAdapter()
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(MessageDetailViewModel::class)
    abstract fun bindViewModel(viewModel: MessageDetailViewModel): ViewModel
}