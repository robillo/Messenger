package com.devswhocare.messenger

import android.app.Application
import com.devswhocare.messenger.di.component.DaggerMessengerAppComponent
import com.devswhocare.messenger.di.component.MessengerAppComponent
import com.devswhocare.messenger.di.module.other.ContentResolverModule
import com.devswhocare.messenger.di.module.other.ContextModule
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.github.inflationx.viewpump.ViewPump
import javax.inject.Inject

class MessengerApp: Application(), HasAndroidInjector {

    private lateinit var component: MessengerAppComponent

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        initComponent()
        initViewPump()
        component.getNotificationUtil().createNotificationChannel()
        AndroidThreeTen.init(this)
    }

    private fun initComponent() {
        component = DaggerMessengerAppComponent.factory().create(
            contentResolver, applicationContext
        )
        component.inject(this)

//        component = DaggerMessengerAppComponent.builder().contextModule(
//            ContextModule(applicationContext)
//        ).contentResolverModule(
//          ContentResolverModule(contentResolver)
//        ).build()
    }

    private fun initViewPump() {
        ViewPump.init(
            ViewPump.builder()
            .addInterceptor(component.calligraphyInterceptor())
            .build()
        )
    }
}