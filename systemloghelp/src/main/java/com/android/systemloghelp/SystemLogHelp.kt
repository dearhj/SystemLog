package com.android.systemloghelp

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.android.systemloglib.IGetTrafficInfoInterface
import com.android.systemloglib.ISystemLogHelpInterface
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

        override fun getNetWorkTrafficData(
            packageName: String?,
            data: IGetTrafficInfoInterface?
        ) {
            if(packageName!= null && context != null) {
                data?.getTrafficData(
                    packageName,
                    getTrafficDataByPackageName(context!!, packageName)[packageName]
                )
            }
        }
    }
}