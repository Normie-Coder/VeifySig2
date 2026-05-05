package com.iveri.plugin.miura.module.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;



import com.miurasystems.mpi.comms.Connector;
import com.miurasystems.mpi.comms.MpiProtocolSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public final class AndroidBluetoothClientConnector extends Connector {

    private static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothDevice mDevice;


    private BluetoothSocket mSocket;

    public AndroidBluetoothClientConnector ( BluetoothDevice device){
        if (BluetoothAdapter.getDefaultAdapter() == null){
            throw new IllegalArgumentException("Bluetooth not supported on this device?");
        }

        mDevice = device;
    }


    @Override
    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    @Override
    protected void connect() throws IOException {
        if (isConnected()){
            return;
        }

        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        mSocket = mDevice.createRfcommSocketToServiceRecord(SERIAL_UUID);
        mSocket.connect();
    }


    @Override
    protected void disconnect( MpiProtocolSession mpiProtocolSession) throws IOException {
        if (!isConnected()){
            return;
        }

        assert  mSocket != null;
        mSocket.close();
        mSocket = null;
    }


    @Override
    protected InputStream getInputStream() throws IOException {
        if (mSocket == null){
            throw new IOException("BluetoothSocket is closed");
        }

        return mSocket.getInputStream();
    }


    @Override
    protected OutputStream getOutputStream() throws IOException {
        if (mSocket == null){
            throw new IOException("BluetoothSocket is closed");
        }

        return mSocket.getOutputStream();
    }
}
