package com.zvonimirplivelic.githound

import android.app.Application
import timber.log.Timber

class GitHoundApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}