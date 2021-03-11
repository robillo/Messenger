package com.devswhocare.messenger.di.module.activity

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.devswhocare.messenger.data.model.Message
import com.devswhocare.messenger.di.mapkey.ViewModelKey
import com.devswhocare.messenger.ui.messages.MessagesActivityViewModel
import com.devswhocare.messenger.ui.messages.adapter.MessageAdapter
import com.devswhocare.productivitylauncher.di.scope.PerActivityScope
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class MessagesActivityModule {

    companion object {

        @Provides
        @PerActivityScope
        fun adapter(comparator: DiffUtil.ItemCallback<Message>): MessageAdapter {
            return MessageAdapter(comparator)
        }

        @Provides
        @PerActivityScope
        fun comparator(): DiffUtil.ItemCallback<Message> {
            return object : DiffUtil.ItemCallback<Message>() {
                override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                    return oldItem.messageId == newItem.messageId
                }
            }
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(MessagesActivityViewModel::class)
    abstract fun bindViewModel(viewModel: MessagesActivityViewModel): ViewModel
}