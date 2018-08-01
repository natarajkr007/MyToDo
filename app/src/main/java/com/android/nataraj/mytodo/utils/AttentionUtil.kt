package com.android.nataraj.mytodo.utils

import android.content.Context
import android.widget.Toast

class AttentionUtil {
    fun alertMessage(context: Context, message: String, duration: Int) {
        Toast.makeText(context, message, duration).show()
    }
}