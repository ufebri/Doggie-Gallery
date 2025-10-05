package com.raylabs.doggie

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled =
            BuildConfig.ENABLE_CRASHLYTICS
    }
}
