package com.example.administrator.paipai.data;


/**
 * 蓝牙设备信息
 */

public class BluetoothDeviceDetail {
    public String address;//设备地址
    public String name;//设备名称
    public int rssi;//设备信号

    public BluetoothDeviceDetail(String name, String address, int rssi){
        this.address = address;
        this.name = name;
        this.rssi = rssi;
    }


    public void setDetail(String name, String address, int rssi){
        this.address = address;
        this.name = name;
        this.rssi = rssi;
    }
}
