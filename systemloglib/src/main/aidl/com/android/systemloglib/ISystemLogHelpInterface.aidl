package com.android.systemloglib;

import com.android.systemloglib.INetworkRecordInterface;
import com.android.systemloglib.IFileUsageRecordInterface;
import com.android.systemloglib.ICameraUsageInfoDataInterface;
import com.android.systemloglib.ILocationUsageInfoDataInterface;
import com.android.systemloglib.IPermissionUsageInfoDataInterface;
import com.android.systemloglib.INfcUsageInfoDataInterface;
import com.android.systemloglib.IAppUsageInfoDataInterface;

interface ISystemLogHelpInterface {
    Map<String, String> getNetWorkTrafficData(in String packageName);
    void getNetworkRecordData(in String packageName, in long previousTime, in INetworkRecordInterface networkRecodeListen);
    void getFileUsageRecordData(in List<String> filePaths, in long previousTime, in IFileUsageRecordInterface fileUsageRecordListen);
    void getCameraUsageInfoData(in ICameraUsageInfoDataInterface cameraUsageInfo);
    void getLocationUsageInfoData(in ILocationUsageInfoDataInterface locationUsageInfo);
    void getPremissionUsageInfoData(in IPermissionUsageInfoDataInterface permissionUsageInfo);
    void getNfcUsageInfoData(in INfcUsageInfoDataInterface nfcUsageInfo);
    void getAppUsageInfoData(in IAppUsageInfoDataInterface appUsageInfo);
    void setAdmin();
    void setDevice();
}