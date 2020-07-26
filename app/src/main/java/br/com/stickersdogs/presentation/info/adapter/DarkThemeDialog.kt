package br.com.stickersdogs.presentation.info.adapter

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import br.com.stickersdogs.R
import kotlinx.android.synthetic.main.dark_theme_dialog.*

class DarkThemeDialog(
    context: Context, private var title: String, private var button: String,
    private val listener: DialogListener
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dark_theme_dialog)

        dialog_title.text = title
        positive_button.text = button

        positive_button.apply {
            setOnClickListener {
                listener.onPositiveClickListener()
            }
        }

        val lp = WindowManager.LayoutParams()

        lp.apply {

            this@DarkThemeDialog.window?.let {
                copyFrom(it.attributes)
            }
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }

        this.window?.let {
            it.setBackgroundDrawableResource(android.R.color.transparent)
            it.attributes = lp
        }

    }

    interface DialogListener {
        fun onNegativeClickListener()
        fun onPositiveClickListener()
    }
}