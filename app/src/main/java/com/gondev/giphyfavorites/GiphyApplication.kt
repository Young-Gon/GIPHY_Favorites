package com.gondev.giphyfavorites

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class GiphyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(object :AndroidLogAdapter(
            PrettyFormatStrategy.newBuilder().apply {
                showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            }.build()
        ){
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }
}