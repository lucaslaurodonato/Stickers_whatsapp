/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package br.com.stickers.presentation.home

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Pair
import android.view.View.GONE
import br.com.stickers.*
import br.com.stickers.presentation.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.ref.WeakReference
import java.util.*
import timber.log.Timber.d as log

class SplashActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, SplashActivity::class.java)
    }

    private var loadListAsyncTask: LoadListAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        overridePendingTransition(0, 0)
        loadListAsyncTask =
            LoadListAsyncTask(this)
        loadListAsyncTask?.execute()
    }

    private fun showStickerPack(stickerPackList: ArrayList<StickerPack>?) {
        entry_activity_progress.visibility = GONE
        if (stickerPackList!!.size > 1) {
            Intent(this, HomeActivity::class.java).let {
                it.putParcelableArrayListExtra(
                    HomeActivity.EXTRA_STICKER_PACK_LIST_DATA,
                    stickerPackList
                )
                startActivity(it)
                finish()
                overridePendingTransition(0, 0)
            }
        } else {
            Intent(this, DetailsPackActivity::class.java).let {
                it.putExtra(DetailsPackActivity.EXTRA_SHOW_UP_BUTTON, false)
                it.putExtra(
                    DetailsPackActivity.EXTRA_STICKER_PACK_DATA,
                    stickerPackList[0]
                )
                startActivity(it)
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }

    private fun showErrorMessage(errorMessage: String?) {
        entry_activity_progress.visibility = GONE
        log("EntryActivity: error fetching sticker packs, $errorMessage")
        error_message.text = getString(R.string.error_message, errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadListAsyncTask != null && !loadListAsyncTask!!.isCancelled) {
            loadListAsyncTask?.cancel(true)
        }
    }

    internal class LoadListAsyncTask(activity: SplashActivity) :
        AsyncTask<Void?, Void?, Pair<String?, ArrayList<StickerPack>?>>() {
        private val contextWeakReference: WeakReference<SplashActivity> = WeakReference(activity)
        override fun doInBackground(vararg voids: Void?): Pair<String?, ArrayList<StickerPack>?> {
            val stickerPackList: ArrayList<StickerPack>
            return try {
                val context: Context? = contextWeakReference.get()
                if (context != null) {
                    stickerPackList =
                        StickerPackLoader.fetchStickerPacks(
                            context
                        )
                    if (stickerPackList.size == 0) {
                        return Pair("could not find any packs", null)
                    }
                    for (stickerPack in stickerPackList) {
                        StickerPackValidator.verifyStickerPackValidity(
                            context,
                            stickerPack
                        )
                    }
                    Pair(null, stickerPackList)
                } else {
                    Pair("could not fetch sticker packs", null)
                }
            } catch (e: Exception) {
                log("EntryActivity: error fetching sticker packs. Exception: $e")
                Pair(e.message, null)
            }
        }

        override fun onPostExecute(stringListPair: Pair<String?, ArrayList<StickerPack>?>) {
            val entryActivity = contextWeakReference.get()
            if (entryActivity != null) {
                if (stringListPair.first != null) {
                    entryActivity.showErrorMessage(stringListPair.first)
                } else {
                    entryActivity.showStickerPack(stringListPair.second)
                }
            }
        }

    }
}