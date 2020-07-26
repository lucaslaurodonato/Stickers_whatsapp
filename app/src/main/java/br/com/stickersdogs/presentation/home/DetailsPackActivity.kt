package br.com.stickersdogs.presentation.home

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.view.View.GONE
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.stickersdogs.R
import br.com.stickersdogs.data.local.SharedPref
import br.com.stickersdogs.presentation.home.StickerPackLoader.getStickerAssetUri
import br.com.stickersdogs.mechanism.validator.WhitelistCheck.isWhitelisted
import br.com.stickersdogs.mechanism.addStickerPack.AddStickerPackActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_details_pack.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import java.lang.ref.WeakReference

class DetailsPackActivity : AddStickerPackActivity() {

    companion object {
        fun getStartIntent(context: Context) =
            Intent(context, DetailsPackActivity::class.java)

        const val EXTRA_STICKER_PACK_ID = "sticker_pack_id"
        const val EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority"
        const val EXTRA_STICKER_PACK_NAME = "sticker_pack_name"
        const val EXTRA_SHOW_UP_BUTTON = "show_up_button"
        const val EXTRA_STICKER_PACK_DATA = "sticker_pack"
    }

    private var recyclerView: RecyclerView? = null
    private var layoutManager: GridLayoutManager? = null
    private var stickerPreviewAdapter: StickerPreviewAdapter? = null
    private var numColumns = 0
    private var stickerPack: StickerPack? = null
    private var whiteListCheckAsyncTask: WhiteListCheckAsyncTask? = null
    private lateinit var sharedPref: SharedPref
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(applicationContext)
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_details_pack)
        stickerPack = intent.getParcelableExtra(EXTRA_STICKER_PACK_DATA)
        setupAdMob()
        setupToolbar()
        setupRecyclerView()
        showInterstitialAd()
    }

    private fun showInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.id_test_intersticial)
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        whiteListCheckAsyncTask =
            WhiteListCheckAsyncTask(
                this
            )
        whiteListCheckAsyncTask?.execute(stickerPack)
    }

    override fun onPause() {
        super.onPause()
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask!!.isCancelled) {
            whiteListCheckAsyncTask?.cancel(true)
        }
    }

    private fun setupRecyclerView() {
        layoutManager = GridLayoutManager(this, 1)
        recyclerView = findViewById(R.id.sticker_list)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.viewTreeObserver?.addOnGlobalLayoutListener(pageLayoutListener)

        if (stickerPreviewAdapter == null) {
            stickerPreviewAdapter =
                StickerPreviewAdapter(
                    layoutInflater,
                    R.drawable.sticker_error,
                    resources.getDimensionPixelSize(R.dimen.sticker_pack_details_image_size),
                    resources.getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding),
                    stickerPack!!
                )
            recyclerView?.adapter = stickerPreviewAdapter
        }
        pack_name.text = stickerPack?.name
        author.text = stickerPack?.publisher
        tray_image.setImageURI(
            getStickerAssetUri(
                stickerPack?.identifier,
                stickerPack?.trayImageFile
            )
        )
        pack_size.text = Formatter.formatShortFileSize(this, stickerPack!!.totalSize)

        add_to_whatsapp_button.setOnClickListener { v: View? ->
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                interstitialAd.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        addStickerPackToWhatsApp(
                            stickerPack!!.identifier,
                            stickerPack!!.name
                        )
                    }
                }
            } else {
                addStickerPackToWhatsApp(
                    stickerPack!!.identifier,
                    stickerPack!!.name
                )
            }
        }
    }

    private val pageLayoutListener = OnGlobalLayoutListener {
        setNumColumns(
            recyclerView!!.width / recyclerView!!.context.resources.getDimensionPixelSize(
                R.dimen.sticker_pack_details_image_size
            )
        )
    }

    private fun setNumColumns(numColumns: Int) {
        if (this.numColumns != numColumns) {
            layoutManager?.spanCount = numColumns
            this.numColumns = numColumns
            if (stickerPreviewAdapter != null) {
                stickerPreviewAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun setupAdMob() {
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun setupToolbar() {
        toolbar.apply {
            toolbar_title.text =
                getString(R.string.title_activity_sticker_pack_details_multiple_pack)
            info.visibility = GONE
            back.setOnClickListener {
                finish()
            }
        }
    }

    private fun updateAddUI(isWhitelisted: Boolean) {
        if (isWhitelisted) {
            add_to_whatsapp_button.isEnabled = false
            text_add_whatsapp.text = getString(R.string.package_already_added)
        } else {
            add_to_whatsapp_button.isEnabled = true
            text_add_whatsapp.text = getString(R.string.add_to_whatsapp)
        }
    }

    internal class WhiteListCheckAsyncTask(listPackActivity: DetailsPackActivity) :
        AsyncTask<StickerPack?, Void?, Boolean>() {
        private val detailsPackActivityWeakReference: WeakReference<DetailsPackActivity> =
            WeakReference(listPackActivity)

        override fun doInBackground(vararg stickerPacks: StickerPack?): Boolean {
            val stickerPack = stickerPacks[0]
            val stickerPackDetailsActivity = detailsPackActivityWeakReference.get()
                ?: return false
            return isWhitelisted(stickerPackDetailsActivity, stickerPack!!.identifier)
        }

        override fun onPostExecute(isWhitelisted: Boolean) {
            val stickerPackDetailsActivity = detailsPackActivityWeakReference.get()
            stickerPackDetailsActivity?.updateAddUI(isWhitelisted)
        }
    }
}