package com.android.systemloghelp

interface CameraInfoDataInterface {
    fun cameraData(pkName: String, enable: Boolean)
}

interface LocationInfoDataInterface {
    fun locationData(pkName: String)
}

interface PermissionInfoDataInterface {
    fun permissionData(pkName: String, name: String, status: Int)
}

interface NfcInfoDataInterface {
    fun nfcData(pkName: String)
}

interface AppInfoDataInterface {
    fun addData(pkName: String, status: Int)
}