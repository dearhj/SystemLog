package com.android.systemloghelp

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.android.systemloglib.IAppUsageInfoDataInterface
import com.android.systemloglib.ICameraUsageInfoDataInterface
import com.android.systemloglib.IFileUsageRecordInterface
import com.android.systemloglib.ILocationUsageInfoDataInterface
import com.android.systemloglib.INetworkRecordInterface
import com.android.systemloglib.INfcUsageInfoDataInterface
import com.android.systemloglib.IPermissionUsageInfoDataInterface
import com.android.systemloglib.ISystemLogHelpInterface
import com.android.systemloglib.deleteLogFile
import com.android.systemloglib.getFileUsageRecordDataList
import com.android.systemloglib.getNetworkRecordDataList
import com.android.systemloglib.getTrafficDataByPackageName
import com.android.systemloglib.write
import com.android.systemloglib.writePackageList

class SystemLogHelp : Service() {
    private val cameraInterface = "com.android.camera.interface.monitor"
    private val permissionInterface = "com.android.permissioncontroller.ops"
    private val locationInterface = "com.android.location.call.api"
    private val nfcInterface = "com.android.nfc.call.api"
    private val applicationInterface = "com.android.application.status"
    private var runningProcessName: MutableList<Pair<Int, String>>? = null


    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        var cameraInfoDataInterface: CameraInfoDataInterface? = null
        var locationInfoDataInterface: LocationInfoDataInterface? = null
        var permissionInfoDataInterface: PermissionInfoDataInterface? = null
        var nfcInfoDataInterface: NfcInfoDataInterface? = null
        var appInfoDataInterface: AppInfoDataInterface? = null
    }

    override fun onBind(intent: Intent?): IBinder {
        context = this
        deleteLogFile()
        writePackageList(this)?.apply {
            try {
                val builder = StringBuilder()
                this.forEach { builder.append(it).append("\n") }
                write(builder.toString())
            } catch (_: Exception) {
            }
        }
        return MyBinder()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        val mFilter = IntentFilter()
        mFilter.addAction(cameraInterface)
        mFilter.addAction(permissionInterface)
        mFilter.addAction(locationInterface)
        mFilter.addAction(nfcInterface)
        mFilter.addAction(applicationInterface)
        registerReceiver(mainReceiver, mFilter)
        runningProcessName = mutableListOf()
        runningProcessName?.clear()
        getAllApplicationByRunningAppProcesses()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mainReceiver)
        } catch (_: Exception) {
        }
    }


    private val mainReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null) {
                when (intent.action) {
                    cameraInterface -> {
                        try {
                            val packageName = intent.getStringExtra("packageName")
                            val isOpen = intent.getBooleanExtra("enable", false)
                            packageName?.let { cameraInfoDataInterface?.cameraData(it, isOpen) }
                        } catch (_: Exception) {
                        }
                    }

                    permissionInterface -> {
                        try {
                            val packageName = intent.getStringExtra("packageName")
                            val permission = intent.getStringExtra("perminsss")
                            val status = intent.getIntExtra("status", -1)
                            if (permission != "" && packageName != "")
                                permissionInfoDataInterface?.permissionData(
                                    packageName!!,
                                    permission!!,
                                    status
                                )
                        } catch (_: Exception) {
                        }
                    }

                    locationInterface -> {
                        try {
                            val packageName = intent.getStringExtra("packageName")
                            if (packageName != "com.android.permissioncontroller") {
                                packageName?.let { locationInfoDataInterface?.locationData(it) }
                            }
                        } catch (_: Exception) {
                        }
                    }

                    nfcInterface -> {
                        try {
                            val packageName = intent.getStringExtra("packageName")
                            packageName?.let { nfcInfoDataInterface?.nfcData(it) }
                        } catch (_: Exception) {
                        }
                    }

                    applicationInterface -> {
                        try {
                            val pid = intent.getIntExtra("pid", -1)
                            val status = intent.getIntExtra("status", -1)
                            var applicationName = getPackageNameFromPid(pid)
                            if (pid != -1 && applicationName != "com.android.launcher3" && status != -1 && applicationName != "com.android.permissioncontroller") {
                                if (status == 0) addPackageName(pid, applicationName)
                                if (status == 2) applicationName = getDiedProgressName(pid)
                                if (!applicationName.contains(":") && applicationName != "") {
                                    appInfoDataInterface?.addData(applicationName, status)
                                }
                            }
                        } catch (_: Exception) {
                        }
                    }
                }
            }
        }
    }

    fun getPackageNameFromPid(pid: Int): String {
        return try {
            val am = (context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            val runningProcess = am.runningAppProcesses
            runningProcess.firstOrNull { it.pid == pid }?.processName ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    private fun getAllApplicationByRunningAppProcesses() {
        try {
            val am = (context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            val runningProcess = am.runningAppProcesses
            runningProcess?.forEach { runningProcessName?.add(Pair(it.pid, it.processName)) }
        } catch (_: Exception) {
        }
    }

    private fun getDiedProgressName(pid: Int): String {
        try {
            runningProcessName?.forEach { if (it.first == pid) return it.second }
        } catch (_: Exception) {
        }
        return ""
    }

    private fun addPackageName(pid: Int, packageName: String) {
        try {
            var needAdd = true
            runningProcessName?.forEach { if (it.first == pid) needAdd = false }
            if (needAdd) runningProcessName?.add(Pair(pid, packageName))
        } catch (_: Exception) {
        }
    }

    class MyBinder : ISystemLogHelpInterface.Stub() {
        override fun getNetWorkTrafficData(packageName: String?): MutableMap<String, String>? {
            try {
                if (packageName != null && context != null) {
                    return getTrafficDataByPackageName(context!!, packageName).toMutableMap()
                }
            } catch (_: Exception) {
            }
            return null
        }

        override fun getNetworkRecordData(
            packageName: String?,
            previousTime: Long,
            networkRecodeListen: INetworkRecordInterface?
        ) {
            try {
                if (packageName != null) {
                    getNetworkRecordDataList(packageName, previousTime).forEach {
                        networkRecodeListen?.networkRecodeInfo(
                            it["packageName"],
                            it["url"],
                            it["logTime"]
                        )
                    }
                }
            } catch (_: Exception) {
            }
        }

        override fun getFileUsageRecordData(
            filePaths: MutableList<String>?,
            previousTime: Long,
            fileUsageRecordListen: IFileUsageRecordInterface?
        ) {
            try {
                if (filePaths != null) {
                    getFileUsageRecordDataList(filePaths, previousTime).forEach {
                        fileUsageRecordListen?.fileUsageRecordInfo(
                            it["packageName"],
                            it["filePath"],
                            it["fileOperateType"],
                            it["logTime"]
                        )
                    }
                }
            } catch (_: Exception) {
            }
        }

        override fun getCameraUsageInfoData(cameraUsageInfo: ICameraUsageInfoDataInterface?) {
            try {
                cameraInfoDataInterface = object : CameraInfoDataInterface {
                    override fun cameraData(pkName: String, enable: Boolean) {
                        cameraUsageInfo?.cameraUsageInfo(pkName, enable)
                    }
                }
            } catch (_: Exception) {
            }
        }

        override fun getLocationUsageInfoData(locationUsageInfo: ILocationUsageInfoDataInterface?) {
            try {
                locationInfoDataInterface = object : LocationInfoDataInterface {
                    override fun locationData(pkName: String) {
                        locationUsageInfo?.locationInfoData(pkName)
                    }
                }
            } catch (_: Exception) {
            }
        }

        override fun getPremissionUsageInfoData(permissionUsageInfo: IPermissionUsageInfoDataInterface?) {
            try {
                permissionInfoDataInterface = object : PermissionInfoDataInterface {
                    override fun permissionData(pkName: String, name: String, status: Int) {
                        permissionUsageInfo?.permissionInfoData(pkName, name, status)
                    }
                }
            } catch (_: Exception) {
            }
        }

        override fun getNfcUsageInfoData(nfcUsageInfo: INfcUsageInfoDataInterface?) {
            try {
                nfcInfoDataInterface = object : NfcInfoDataInterface {
                    override fun nfcData(pkName: String) {
                        nfcUsageInfo?.nfcInfoData(pkName)
                    }
                }
            } catch (_: Exception) {
            }
        }

        override fun getAppUsageInfoData(appUsageInfo: IAppUsageInfoDataInterface?) {
            try {
                appInfoDataInterface = object : AppInfoDataInterface {
                    override fun addData(pkName: String, status: Int) {
                        appUsageInfo?.appInfoData(pkName, status)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }
}