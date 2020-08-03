package com.gondev.giphyfavorites

import android.app.Application
import com.gondev.movie.util.timber.DebugLogTree
import com.gondev.movie.util.timber.ReleaseLogTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class GiphyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugLogTree())
        } else {
            Timber.plant(ReleaseLogTree())
        }
    }
}