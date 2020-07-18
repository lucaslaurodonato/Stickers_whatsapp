package br.com.stickers.data.local

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {

    private val mySharedPref: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setNightModeState(state: Boolean) {
        val editor = mySharedPref.edit()
        state?.let { editor.putBoolean("Dark", it) }
        editor.apply()
    }

    fun loadNightModeState(): Boolean {
        return mySharedPref.getBoolean("Dark", false)
    }

}
