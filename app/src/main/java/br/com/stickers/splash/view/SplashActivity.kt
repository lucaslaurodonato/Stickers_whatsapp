package br.com.stickers.splash.view

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import br.com.stickers.EntryActivity
import br.com.stickers.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startActivity(EntryActivity.getStartIntent(this))
            finish()
        }, 3000.toLong())
    }
}