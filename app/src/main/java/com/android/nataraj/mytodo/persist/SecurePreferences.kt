package com.android.nataraj.mytodo.persist

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

object SecurePreferences {

    fun putBoolean(sharedPreferences: SharedPreferences, key: String, value: Boolean) {
        var editor: Editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }
}