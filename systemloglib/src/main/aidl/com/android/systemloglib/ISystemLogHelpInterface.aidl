package com.android.systemloglib;

import com.android.systemloglib.IGetTrafficInfoInterface;

interface ISystemLogHelpInterface {
    void getNetWorkTrafficData(in String packageName, in IGetTrafficInfoInterface data);
}