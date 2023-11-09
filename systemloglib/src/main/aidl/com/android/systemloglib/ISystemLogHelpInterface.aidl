package com.android.systemloglib;

import com.android.systemloglib.INetworkRecordInterface;
import com.android.systemloglib.IFileUsageRecordInterface;

interface ISystemLogHelpInterface {
    Map<String, String> getNetWorkTrafficData(in String packageName);
    void getNetworkRecordData(in String packageName, in long previousTime, in INetworkRecordInterface networkRecodeListen);
    void getFileUsageRecordData(in List<String> filePaths, in long previousTime, in IFileUsageRecordInterface fileUsageRecordListen);
}