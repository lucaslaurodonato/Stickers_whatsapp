package br.com.stickers.presentation.info.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import br.com.stickers.R
import br.com.stickers.data.local.SharedPref
import br.com.stickers.mechanism.AppUtils
import br.com.stickers.presentation.home.SplashActivity
import br.com.stickers.presentation.base.view.BaseActivity
import br.com.stickers.presentation.info.adapter.DarkThemeDialog
import kotlinx.android.synthetic.main.activity_details_pack.toolbar
import kotlinx.android.synthetic.main.activity_sticker_pack_info.*
import kotlinx.android.synthetic.main.include_toolbar.view.*

class StickerPackInfoActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, StickerPackInfoActivity::class.java)
    }

    private lateinit var sharedPref: SharedPref
    private var darkModeDialog: DarkThemeDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(applicationContext)
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_sticker_pack_info)
        setupToolbar()
        appVersion()
        darkMode()
        setupFields()
        receiveDataToSetup()
    }

    private fun setupDialogLogout() {
        val title: String = if (sharedPref.loadNightModeState()) {
            getString(R.string.dark_mode_on)
        } else {
            getString(R.string.dark_mode_off)
        }

        darkModeDialog =
            DarkThemeDialog(
                this,
                title = title,
                button = getString(R.string.dark_mode_restart_button),
                listener = object : DarkThemeDialog.DialogListener {
                    override fun onNegativeClickListener() {}

                    override fun onPositiveClickListener() {
                        restartApplication()
                    }
                }).apply {
                setCancelable(false)
                show()
            }
    }

    private fun darkMode() {
        if (sharedPref.loadNightModeState()) {
            dark_mode.isChecked = true
        }
        dark_mode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedPref.setNightModeState(true)
                setupDialogLogout()
            } else {
                sharedPref.setNightModeState(false)
                setupDialogLogout()
            }
        }
    }

    private fun restartApplication() {
        Intent(applicationContext, SplashActivity::class.java).let {
            startActivity(it)
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun appVersion() {
        app_version.text =
            getString(R.string.app_version_settings, AppUtils.version(applicationContext))
    }

    private fun receiveDataToSetup() {
        val email = getString(R.string.email_support)
        send_email.setOnClickListener {
            launchEmailClient(email)
        }
    }

    private fun setupToolbar() {
        toolbar.apply {
            toolbar_title.text = getString(R.string.title_activity_sticker_pack_info)
            info.visibility = GONE
            back.setOnClickListener {
                finish()
            }
        }
    }

    private fun launchEmailClient(email: String) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        startActivity(
            Intent.createChooser(
                emailIntent,
                resources.getString(R.string.info_send_email_to_prompt)
            )
        )
    }

    private fun setupFields() {
        val website = getString(R.string.linkedin_support)
        view_webpage.setOnClickListener {
            launchWebPage(website)
        }
    }

    private fun launchWebPage(website: String) {
        val uri = Uri.parse(website)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}