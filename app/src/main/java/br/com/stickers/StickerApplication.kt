/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package br.com.stickers

import android.app.Application
import br.com.stickers.BuildConfig.DEBUG
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.analytics.FirebaseAnalytics
import br.com.stickers.logging.FabricTree
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