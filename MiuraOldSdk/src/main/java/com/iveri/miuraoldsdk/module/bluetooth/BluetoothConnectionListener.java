package com.iveri.plugin.miura.module.bluetooth;

public interface BluetoothConnectionListener {

    void onConnected();

    void onDisconnected();

    void onConnectionAttemptFailed();
}
