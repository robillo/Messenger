package com.devswhocare.messenger.di.module.other

import androidx.lifecycle.ViewModelProvider
import com.devswhocare.messenger.util.DaggerViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(
        viewModelFactory: DaggerViewModelFactory
    ): ViewModelProvider.Factory
}