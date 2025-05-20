package com.savelyev.MobileApp.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

@SuppressLint("StaticFieldLeak")
object PushManager {
    private var context: Context? = null
    private const val TAG = "PushManager"

    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT, context: Context?  = null) {
        if (context != null )
        {
            Toast.makeText(context, message, duration).show()
        }
        this.context?.let {
            Toast.makeText(it, message, duration).show()
        } ?: run {
            throw IllegalStateException("PushManager not initialized. Call init() first.")
        }
    }

    fun showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT, context: Context? = null) {
        if (context != null )
        {
            Toast.makeText(context, resId, duration).show()
        }
        this.context?.let {
            Toast.makeText(it, resId, duration).show()
        } ?: run {
            throw IllegalStateException("PushManager not initialized. Call init() first.")
        }
    }
}