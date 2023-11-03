package com.android.systemloglib.initializer

import android.content.Context
import android.content.Intent
import androidx.startup.Initializer
import com.android.systemloglib.MonitorInterfaceService

class MonitorInitializer : Initializer<Unit> {

    override fun create(context: Context) {

        try {
            context.startService(Intent(context, MonitorInterfaceService::class.java))
        } catch (_: Exception) {
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}