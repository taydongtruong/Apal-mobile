package com.admin.apal.data

import android.content.Context
import android.content.SharedPreferences

object SharedPrefs {
    private const val PREF_NAME = "apal_prefs"
    private const val KEY_TOKEN = "access_token"
    private const val KEY_ROLE = "user_role"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuth(context: Context, token: String, role: String) {
        val editor = getPrefs(context).edit()
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun getToken(context: Context): String? = getPrefs(context).getString(KEY_TOKEN, null)
    fun getRole(context: Context): String? = getPrefs(context).getString(KEY_ROLE, null)

    fun clear(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}