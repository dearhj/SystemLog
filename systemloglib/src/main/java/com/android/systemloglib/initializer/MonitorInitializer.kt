package com.android.systemloglib.initializer

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

class MonitorInitializer : Initializer<Unit> {
    companion object{
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null
    }

    override fun create(context: Context) {
        mContext = context
//        initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}