package com.android.systemloglib;

interface IFileUsageRecordInterface {
    void fileUsageRecordInfo(in String packageName, in String filePath, in String fileOperateType, in String logTime);
}