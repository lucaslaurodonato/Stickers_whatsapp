package br.com.stickers.logging

import android.app.Application
import br.com.stickers.BuildConfig.DEBUG
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class StickerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)

        // Logging
        if (DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
//        Timber.plant(FabricTree())

        // Analytics
        FirebaseAnalytics.getInstance(this)
    }
}