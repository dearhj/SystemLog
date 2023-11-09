package com.android.systemloglib

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
        val intent = Intent()
        intent.action = "com.android.systemloghelp.LogHelpService"
        intent.setPackage("com.android.systemloghelp")
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
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
        unbind(context)
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
    try {
        mService?.getFileUsageRecordData(
            filePaths,
            previousTime,
            object : IFileUsageRecordInterface.Stub() {
                override fun fileUsageRecordInfo(
                    packageNameResult: String?,
                    filePathResult: String?,
                    fileOperateTypeResult: String?,
                    logTimeResult: String?
                ) {
                    returnList.add(
                        mapOf(
                            "packageName" to (packageNameResult ?: ""),
                            "filePath" to (filePathResult ?: ""),
                            "fileOperateType" to (fileOperateTypeResult ?: ""),
                            "logTime" to (logTimeResult ?: "")
                        )
                    )
                }
            })
    } catch (_: Exception) {
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
    try {
        mService?.getNetworkRecordData(
            packageName,
            previousTime,
            object : INetworkRecordInterface.Stub() {
                override fun networkRecodeInfo(
                    packageNameResult: String?,
                    urlResult: String?,
                    longTimeResult: String?
                ) {
                    returnList.add(
                        mapOf(
                            "packageName" to (packageNameResult ?: ""),
                            "url" to (urlResult ?: ""),
                            "logTime" to (longTimeResult ?: "")
                        )
                    )
                }
            })
    } catch (_: Exception) {
    }
    return returnList
}


/**
 * @description 获取流量接口
 * @param packageName 包名,这里的流量是从设备开机时开始计算的
 * @return packageName,traffic(使用的字节数)
 */
@SuppressLint("MissingPermission", "HardwareIds")
fun getTrafficByPackageName(
    packageName: String
): Map<String, String>? {
    var data: Map<String, String>? = null
    try {
        data = mService?.getNetWorkTrafficData(packageName)
    } catch (_: Exception) {
    }
    return data
}
