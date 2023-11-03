package com.android.systemloglib

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.os.Build
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun now(): String = SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss", Locale.CHINA
).format(Date(System.currentTimeMillis()))

fun writePackageList(context: Context): List<String>? {
    var list: List<String>? = null
    try {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_NEW_TASK
        val queryIntentActivities: List<ResolveInfo> = pm.queryIntentActivities(intent, 0x00002000)
        list = queryIntentActivities.map { it.activityInfo.packageName }.toList()
    } catch (_: Exception) {
    }
    return list
}

@SuppressLint("SdCardPath")
fun write(context: String) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Files.write(
                Paths.get("/sdcard/packages"),
                context.toByteArray(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
            )
        }
    } catch (_: Exception) {
    }
}

fun getTimesMonthMorning(): Long {
    val cal = Calendar.getInstance()
    cal.set(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH),
        0,
        0,
        0
    )
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
    return cal.timeInMillis
}

fun getUid(context: Context, packageName: String): Int {
    var uid = -1
    val pm = context.packageManager
    try {
        val applicationInfo: ApplicationInfo = pm.getApplicationInfo(packageName, 0)
        uid = applicationInfo.uid
    } catch (_: Exception) {
    }
    return uid
}

fun read2(path: String?): List<String?>? {
    var lines: List<String?>? = null
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val file = Paths.get(path)
            lines = Files.readAllLines(file)
        }
    } catch (_: Exception) {
    }
    return lines
}

lateinit var cameraListener: (String, Boolean) -> Unit
lateinit var gpsListener: (String) -> Unit
lateinit var permissionListener: (String, String?, Int) -> Unit
lateinit var nfcListener: (String) -> Unit
lateinit var applicationListener: (String, Int) -> Unit

