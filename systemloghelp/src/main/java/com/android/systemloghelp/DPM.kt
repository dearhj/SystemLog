package com.android.systemloghelp

import android.app.admin.DevicePolicyManager
import android.app.admin.IDevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.ServiceManager

/**
 * 静默激活设备管理器
 */
fun setActiveAdmin(componentName: ComponentName) {
    IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy"))
        .setActiveAdmin(componentName, true, 0)
}

/**
 * 激活admin 后直接调用此方法
 */
fun setProfileOwner(componentName: ComponentName) {
    IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy"))
        .setProfileOwner(componentName, componentName.packageName, 0)
}

/**
 * 激活admin 后直接调用此方法
 */
fun setDeviceOwner(componentName: ComponentName) {
    IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy")).setDeviceOwner(componentName, componentName.packageName, 0)
}

/**
 * 判断是否激活设备管理器
 */
fun isAdminActive(context: Context, componentName: ComponentName): Boolean {
    return (context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager)
        .isAdminActive(componentName)
}

/**
 * 判断此包名是否以及申请了DPM权限
 */
fun isProfileOwnerApp(context: Context, packageName: String): Boolean {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    return dpm.isProfileOwnerApp(packageName)
}

fun isDeviceOwnerApp(context: Context, packageName: String): Boolean {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    return dpm.isDeviceOwnerApp(packageName)
}


fun setActiveProfileOwner(componentName: ComponentName) {
    try {
        setActiveAdmin(componentName)
        setProfileOwner(componentName)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
