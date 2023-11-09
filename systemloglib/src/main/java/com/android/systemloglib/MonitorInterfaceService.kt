package com.android.systemloglib

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import java.lang.Thread.sleep

class MonitorInterfaceService : Service() {
    private val cameraInterface = "com.android.camera.interface.monitor"
    private val permissionInterface = "com.android.permissioncontroller.ops"
    private val locationInterface = "com.android.location.call.api"
    private val nfcInterface = "com.android.nfc.call.api"
    private val applicationInterface = "com.android.application.status"
    private var mContext: Context? = null
    private var runningProcessName: MutableList<Pair<Int, String>>? = null


    override fun onBind(intent: Intent): IBinder {
        TODO()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        val mFilter = IntentFilter()
        mContext = this
        mFilter.addAction(cameraInterface)
        mFilter.addAction(permissionInterface)
        mFilter.addAction(locationInterface)
        mFilter.addAction(nfcInterface)
        mFilter.addAction(applicationInterface)
        registerReceiver(mainReceiver, mFilter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            Thread {
                while (mService == null) {
                    sleep(50)
                }
                runningProcessName = mutableListOf()
                runningProcessName?.clear()
                mService?.getAllApplicationByRunningAppProcesses(object :
                    IApplicationInfoInterface.Stub() {
                    override fun applicationInfoList(packageName: String?, pid: Int) {
                        runningProcessName?.add(Pair(pid, packageName ?: ""))
                    }
                })
            }.start()
        } catch (_: Exception) {
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private val mainReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null) {
                when (intent.action) {
                    cameraInterface -> {
                        val packageName = intent.getStringExtra("packageName")
                        val isOpen = intent.getBooleanExtra("enable", false)
                        packageName?.let { cameraListener(packageName, isOpen) }
                    }

                    permissionInterface -> {
                        val packageName = intent.getStringExtra("packageName")
                        val permission = intent.getStringExtra("perminsss")
                        val status = intent.getIntExtra("status", -1)
                        packageName?.let { permissionListener(it, permission, status) }
                    }

                    locationInterface -> {
                        val packageName = intent.getStringExtra("packageName")
                        packageName?.let { gpsListener(it) }
                    }

                    nfcInterface -> {
                        val packageName = intent.getStringExtra("packageName")
                        packageName?.let { nfcListener(it) }
                    }

                    applicationInterface -> {
                        val pid = intent.getIntExtra("pid", -1)
                        val status = intent.getIntExtra("status", -1)
                        var applicationName = mService?.getApplicationByPid(pid) ?: ""
                        if (pid != -1 && applicationName != "com.android.launcher3" && status != -1) {
                            if (status == 0) addPackageName(pid, applicationName)
                            if (status == 2) applicationName = getDiedProgressName(pid)
                            applicationListener(applicationName, status)
                        }
                    }
                }
            }
        }
    }

    private fun getDiedProgressName(pid: Int): String {
        runningProcessName?.forEach { if (it.first == pid) return it.second }
        return ""
    }

    private fun addPackageName(pid: Int, packageName: String) {
        var needAdd = true
        runningProcessName?.forEach { if (it.first == pid) needAdd = false }
        if (needAdd) runningProcessName?.add(Pair(pid, packageName))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mainReceiver)
    }

}