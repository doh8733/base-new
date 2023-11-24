package com.example.colorphone.util

import android.content.SharedPreferences
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtil
@Inject
constructor(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {

    var token: String?
        get() = sharedPreferences.getString("CachedToken", null)
        set(value) {
            editor.putString("CachedToken", value).commit()
        }

    var isVibrate: Boolean
        get() = sharedPreferences.getBoolean("isVibrate", false)
        set(value) {
            editor.putBoolean("isVibrate", value).commit()
        }

    var IS_FO: Boolean
        get() = sharedPreferences.getBoolean("IS_FO", true)
        set(value) {
            editor.putBoolean("IS_FO", value).commit()
        }

    var IS_POLICY: Boolean
        get() = sharedPreferences.getBoolean("IS_POLICY", true)
        set(value) {
            editor.putBoolean("IS_POLICY", value).commit()
        }

    var isRate: Boolean
        get() = sharedPreferences.getBoolean("isRate", false)
        set(value) {
            editor.putBoolean("isRate", value).commit()
        }

    var isShowRateToDay: Boolean
        get() = sharedPreferences.getBoolean("isShowRate", false)
        set(value) {
            editor.putBoolean("isShowRate", value).commit()
        }

}