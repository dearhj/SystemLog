// IGetTrafficInfoInterface.aidl
package com.android.systemloglib;

// Declare any non-default types here with import statements

interface IGetTrafficInfoInterface {
    void getTrafficData(in String packageName, in String trafficNum);
}