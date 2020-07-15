package br.com.stickers.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class FabricTree : Timber.Tree() {

    override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?
    ) {
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        if (t != null) {
            firebaseCrashlytics.recordException(t)
        } else {
            firebaseCrashlytics.log("E/$tag: $message")
        }
    }
}