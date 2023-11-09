package com.android.systemloglib;

import com.android.systemloglib.INetworkRecordInterface;

interface ISystemLogHelpInterface {
    Map<String, String> getNetWorkTrafficData(in String packageName);
    void getNetworkRecordData(in String packageName, in long previousTime, in INetworkRecordInterface networkRecodeListen);
}