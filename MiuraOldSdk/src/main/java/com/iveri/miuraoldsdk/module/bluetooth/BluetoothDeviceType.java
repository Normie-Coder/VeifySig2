package com.iveri.plugin.miura.module.bluetooth;

public enum BluetoothDeviceType {

    PED("Miura"),
    POS("POS"),
    ITP("ITP");

    private String deviceTypeName;

    BluetoothDeviceType (String deviceName){
        this.deviceTypeName = deviceName;
    }

    public String getDeviceTypeName(){
        return deviceTypeName;
    }

    public static BluetoothDeviceType getByDeviceTypeByName(String deviceName){
        if (deviceName.toLowerCase().contains(POS.name().toLowerCase()) || deviceName.toLowerCase().contains(ITP.name().toLowerCase())){
            return POS;
        }
        else{
            return PED;
        }
    }
}
