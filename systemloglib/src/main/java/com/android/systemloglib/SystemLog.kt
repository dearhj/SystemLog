package com.android.systemloglib

import android.annotation.SuppressLint
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.telephony.TelephonyManager
import android.text.TextUtils


/**
 * @description 注册监听
 * @param context 上下文
 * @return null
 */
fun registerListenInterface(context: Context) {
    writePackageList(context)?.apply {
        try {
            val builder = StringBuilder()
            this.forEach { builder.append(it).append("\n") }
            write(builder.toString())
        } catch (_: Exception) {
        }
    }
    try {
        context.startService(Intent(context, MonitorInterfaceService::class.java))
    } catch (_: Exception) {
    }
}

/**
 * @description 取消注册监听
 * @param context 上下文
 * @return null
 */
fun unRegisterListenInterface(context: Context) {
    try {
        context.stopService(Intent(context, MonitorInterfaceService::class.java))
    } catch (_: Exception) {
    }
}

/**
 * @description 摄像头调用日志的接口
 * @param onChange 回调接口，其中第一个参数为调用者包名，第二个参数为打开或者关闭摄像头(true/false)
 * @return null
 */
fun setCameraUsageListener(onChange: (String, Boolean) -> Unit) {
    cameraListener = onChange
}

/**
 * @description GPS调用日志的接口
 * @param onChange 回调接口，其中参数为调用GPS的应用包名
 * @return null
 */
fun setGpsUsageListener(onChange: (String) -> Unit) {
    gpsListener = onChange
}

/**
 * @description 权限授权日志接口
 * @param onChange 回调接口，其中第一个参数为请求权限的包名，第二个参数为请求的权限，第三个参数为授权情况
 * @return null
 */
fun setAppPermissionRequestListener(onChange: (String, String?, Int) -> Unit) {
    permissionListener = onChange
}

/**
 * @description NFC调用日志接口
 * @param onChange 回调接口，其中参数为调用NFC的应用包名
 * @return null
 */
fun setNfcUsageListener(onChange: (String) -> Unit) {
    nfcListener = onChange
}

/**
 * @description 软件打开关闭日志接口
 * @param onChange 回调接口，其中第一个参数为应用报包名，第二个参数中为状态：0为应用打开，1为应用关闭，2为应用进程结束
 * @return null
 */
fun setAppUsageListener(onChange: (String, Int) -> Unit) {
    applicationListener = onChange
}

/**
 * @description 获取文件日志接口
 * @param filePaths 过滤的列表
 * @param previousTime 从现在到以前的时间段
 * @return List<Map<String, String>> 数据集合  packageName(包名)、filePath(文件路径)，fileOperateType(增删改查)、logTime(日志时间)
 */
@SuppressLint("SdCardPath")
fun getFileUsageRecordList(
    filePaths: List<String>, previousTime: Long
): List<Map<String, String>> {
    val returnList = mutableListOf<Map<String, String>>()
    val list = read2("/sdcard/log")
    list?.forEach {
        try {
            it?.apply {
                val splits = it.split(",")
                val time = splits[0]
                val packageName = splits[1]
                val typeItems = splits[2].split("=")
                val typeStr = when (typeItems[0]) {
                    "createNewFile" -> "create"
                    "delete" -> "delete"
                    "FileInputStream" -> "input"
                    "FileOutputStream" -> "output"
                    else -> ""
                }
                if (!TextUtils.isEmpty(typeStr)) {
                    val path = typeItems[1]
                    val timeCount = System.currentTimeMillis() - previousTime
                    if (filePaths.any { whitePath -> path.startsWith(whitePath) } && time.toLong() >= timeCount) {
                        returnList.add(
                            mapOf(
                                "packageName" to packageName,
                                "filePath" to path,
                                "fileOperateType" to typeStr,
                                "logTime" to time
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }
    }
    return returnList
}

/**
 * @description 获取网络流量日志接口
 * @param packageName 包名
 * @param previousTime 从现在到以前的时间段
 * @return List<Map<String, String>> 数据集合   packageName(包名)、url(请求连接/请求域)、logTime(日志时间)
 */
@SuppressLint("SdCardPath")
fun getNetworkRecordList(
    packageName: String, previousTime: Long
): List<Map<String, String>> {
    val returnList = mutableListOf<Map<String, String>>()
    val list = read2("/sdcard/log")
    list?.forEach {
        try {
            it?.apply {
                val splits = it.split(",")
                val time = splits[0]
                val packageName2 = splits[1]
                val typeItems = splits[2].split("=")
                val typeStr = typeItems[0]
                if ("openConnection" == typeStr || "SocketgetInputStream" == typeStr) {
                    val timeCount = System.currentTimeMillis() - previousTime
                    if (packageName2 == packageName && time.toLong() >= timeCount) {
                        val url = typeItems[1]
                        returnList.add(
                            mapOf(
                                "packageName" to packageName2,
                                "url" to url,
                                "logTime" to time
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }
    }
    return returnList
}


/**
 * @description 获取流量接口
 * @param context 上下文
 * @param packageName 包名
 * @return packageName,traffic(使用的字节数)
 */
@SuppressLint("MissingPermission", "HardwareIds")
fun getTrafficByPackageName(
    context: Context, packageName: String
): Map<String, String> {
    try {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val subId = tm.subscriberId
        println("获取到的SubId=${subId}")
        val nsm =
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        nsm.querySummary(
            ConnectivityManager.TYPE_MOBILE,
            subId,
            getTimesMonthMorning(),
            System.currentTimeMillis()
        )
    } catch (_: Exception) {
    }
    val rx = TrafficStats.getUidRxBytes(getUid(context, packageName))
    val tx = TrafficStats.getUidTxBytes(getUid(context, packageName))
    return mapOf(packageName to "${tx + rx}")
}
