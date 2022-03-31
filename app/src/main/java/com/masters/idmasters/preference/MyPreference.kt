package com.masters.idmasters.preference

import android.content.Context
import android.content.SharedPreferences

class MyPreference() {

    fun getString(key: String): String? {
        return sharedPreferences!!.getString(key, "")
    }

    companion object {
        private val mySharedPreference = MyPreference()
        private var sharedPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        fun getInstance(context: Context?): MyPreference {
            if (sharedPreferences == null) {
                sharedPreferences =
                    context!!.getSharedPreferences("my", Context.MODE_PRIVATE)
            }
            return mySharedPreference
        }
    }

    fun setString(key: String, text: String?) {
        editor = sharedPreferences?.edit()
        editor?.putString(key, text)
        editor?.apply()
    }

    fun clearCache() {
        editor!!.clear()
    }

}