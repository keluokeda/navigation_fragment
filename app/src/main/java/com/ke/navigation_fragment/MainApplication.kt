package com.ke.navigation_fragment

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter(PrettyFormatStrategy
            .newBuilder()
            .showThreadInfo(false)
            .methodCount(1)
            .build()))
    }
}