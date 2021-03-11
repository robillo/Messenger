package com.devswhocare.messenger.di.module.other

import com.devswhocare.messenger.R
import com.devswhocare.messenger.di.scope.MessengerAppScope
import com.devswhocare.messenger.util.Constants.fontPathMontserratRegular
import dagger.Module
import dagger.Provides
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor

@Module
object MessengerAppModule {

    @Provides
    @MessengerAppScope
    fun calligraphyInterceptor(): CalligraphyInterceptor {
        return CalligraphyInterceptor(
            CalligraphyConfig.Builder()
                .setDefaultFontPath(fontPathMontserratRegular)
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}