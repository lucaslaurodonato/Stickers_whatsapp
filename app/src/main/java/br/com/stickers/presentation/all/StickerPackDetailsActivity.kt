package br.com.stickers.presentation.all

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
import br.com.stickers.R
import br.com.stickers.data.local.SharedPref
import br.com.stickers.presentation.all.StickerPackLoader.getStickerAssetUri
import br.com.stickers.mechanism.validator.WhitelistCheck.isWhitelisted
import br.com.stickers.mechanism.addStickerPack.AddStickerPackActivity
import br.com.stickers.presentation.info.StickerPackInfoActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_sticker_pack_details.*
import kotlinx.android.synthetic.main.include_toolbar.view.*
import java.lang.ref.WeakReference

class StickerPackDetailsActivity : AddStickerPackActivity() {

    companion object {
        fun getStartIntent(context: Context) =
            Intent(context, StickerPackDetailsActivity::class.java)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(applicationContext)
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_sticker_pack_details)
        stickerPack = intent.getParcelableExtra(EXTRA_STICKER_PACK_DATA)
        setupAdMob()
        setupToolbar()
        setupRecyclerView()
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
            addStickerPackToWhatsApp(
                stickerPack!!.identifier,
                stickerPack!!.name
            )
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

    internal class WhiteListCheckAsyncTask(stickerPackListActivity: StickerPackDetailsActivity) :
        AsyncTask<StickerPack?, Void?, Boolean>() {
        private val stickerPackDetailsActivityWeakReference: WeakReference<StickerPackDetailsActivity> =
            WeakReference(stickerPackListActivity)

        override fun doInBackground(vararg stickerPacks: StickerPack?): Boolean {
            val stickerPack = stickerPacks[0]
            val stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get()
                ?: return false
            return isWhitelisted(stickerPackDetailsActivity, stickerPack!!.identifier)
        }

        override fun onPostExecute(isWhitelisted: Boolean) {
            val stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get()
            stickerPackDetailsActivity?.updateAddUI(isWhitelisted)
        }
    }
}