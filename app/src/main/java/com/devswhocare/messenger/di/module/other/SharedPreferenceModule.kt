package com.devswhocare.messenger.di.module.other

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.devswhocare.messenger.di.scope.MessengerAppScope
import com.devswhocare.messenger.util.SharedPreferenceUtil
import dagger.Module
import dagger.Provides

@Module
object SharedPreferenceModule {

    @Provides
    @MessengerAppScope
    fun sharedPreferenceUtil(preferences: SharedPreferences): SharedPreferenceUtil {
        return SharedPreferenceUtil(preferences)
    }

    @Provides
    @MessengerAppScope
    fun preferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}