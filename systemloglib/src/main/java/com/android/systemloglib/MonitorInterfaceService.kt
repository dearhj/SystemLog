package com.android.systemloglib

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class MonitorInterfaceService : Service() {
    private val cameraInterface = "com.android.camera.interface.monitor"
    private val permissionInterface = "com.android.permissioncontroller.ops"
    private val locationInterface = "com.android.location.call.api"
    private val nfcInterface = "com.android.nfc.call.api"
    private val applicationInterface = "com.android.application.status"
    private var mContext: Context? = null
    private var am: ActivityManager? = null
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

        am = (this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        val runningProcess = am?.runningAppProcesses
        runningProcessName = mutableListOf()
        runningProcessName?.clear()
        runningProcess?.forEach { runningProcessName?.add(Pair(it.pid, it.processName)) }
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
                        if (pid != -1 && getPackageFromPid2(pid) != "com.android.launcher3" && status != -1) {
                            if (status == 0) addPackageName(pid)
                            val packageName = if (status == 2) getDiedProgressName(pid)
                            else getPackageFromPid2(pid)
                            applicationListener(packageName, status)
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

    private fun addPackageName(pid: Int) {
        var needAdd = false
        runningProcessName?.forEach { if (it.first != pid) needAdd = true }
        if (needAdd) runningProcessName?.add(Pair(pid, getPackageFromPid2(pid)))
    }

    fun getPackageFromPid2(pid: Int): String {
        val am = (mContext?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        val runningProcess = am.runningAppProcesses
        return runningProcess.firstOrNull { it.pid == pid }?.processName ?: ""
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mainReceiver)
    }

}