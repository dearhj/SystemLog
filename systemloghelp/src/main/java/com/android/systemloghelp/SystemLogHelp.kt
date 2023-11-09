package com.android.systemloghelp

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.android.systemloglib.INetworkRecordInterface
import com.android.systemloglib.ISystemLogHelpInterface
import com.android.systemloglib.getNetworkRecordDataList
import com.android.systemloglib.getTrafficDataByPackageName

class SystemLogHelp : Service() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }

    override fun onBind(intent: Intent?): IBinder {
        context = this
        return MyBinder()
    }

    class MyBinder : ISystemLogHelpInterface.Stub() {
        override fun getNetWorkTrafficData(packageName: String?): MutableMap<String, String>? {
            if (packageName != null && context != null) {
                return getTrafficDataByPackageName(context!!, packageName).toMutableMap()
            }
            return null
        }

        override fun getNetworkRecordData(
            packageName: String?,
            previousTime: Long,
            networkRecodeListen: INetworkRecordInterface?
        ) {
            if (packageName != null) {
                getNetworkRecordDataList(packageName, previousTime).forEach {
                    networkRecodeListen?.networkRecodeInfo(
                        it["packageName"],
                        it["url"],
                        it["logTime"]
                    )
                }
            }
        }
    }
}