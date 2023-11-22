package com.android.systemlog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.android.systemloglib.registerListenInterface
import com.android.systemloglib.setAppPermissionRequestListener
import com.android.systemloglib.setAppUsageListener
import com.android.systemloglib.setCameraUsageListener
import com.android.systemloglib.setNfcUsageListener
import com.android.systemloglib.unRegisterListenInterface


class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    private var notificationManager: NotificationManager? = null
    private var notification: Notification? = null
    private var notificationChannel: NotificationChannel? = null
    private val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID"
    private val NOTIFICATION_CHANNEL_NAME = "CHANNEL_NAME"
    private val FOREGROUND_ID = 1

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel!!.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(notificationChannel!!)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("hello")
                .setContentText("world")
                .build()
        }
        notification!!.flags = notification!!.flags or Notification.FLAG_NO_CLEAR
        startForeground(FOREGROUND_ID, notification)
        return START_STICKY
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        registerListenInterface(this)
        println("mhj  监控到了后台服务启动，")
        setCameraUsageListener { packageName, enable ->
            println("mhj  这里是最终结果，有数据了吗》？？      $packageName     $enable")
        }

        setNfcUsageListener { packageName ->
            println("监控到了NFC调用相关数据？    $packageName    ")
        }

        setAppPermissionRequestListener { packageName, permission, status ->
            println("监控到了权限授权相关数据？    $packageName    $permission      $status ")
        }

        setAppUsageListener { packageName, status ->
            println("监控到了应用启用相关数据？    $packageName     $status ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("mhj  myservice 服务结束！！！！！")
        unRegisterListenInterface(this)
    }
}