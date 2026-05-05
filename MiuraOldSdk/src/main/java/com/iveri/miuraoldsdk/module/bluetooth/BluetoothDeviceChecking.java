package com.iveri.plugin.miura.module.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
//import android.util.Log;

import com.iveri.plugin.miura.exception.mPressException;

import java.util.ArrayList;

public class BluetoothDeviceChecking {

    public enum Mode {
        checkAll, checkOnlySelected, noChecking
    }

    private ArrayList<BluetoothDevice> selectedDevices, availableDevices;
    private ArrayList<BluetoothDevice> checkedSelectedDevices, checkedAvailableDevices;
    private DevicesListener devicesListener;
    private int indexPaired =0, indexNonPaired =0;
    private Mode mode = Mode.noChecking;

    public BluetoothDeviceChecking(Context context, Mode mode, DevicesListener devicesListener) throws mPressException {
        this.devicesListener = devicesListener;
        this.mode = mode;
     // Log.d("Testing", "BluetoothDeviceChecking: before getPairedDevices >>>>>>>>>>>>>>>>>>>>>>>>>>>");
        selectedDevices = BluetoothModule.getInstance().getPairedDevices(context);
     // Log.d("Testing", "BluetoothDeviceChecking: after getPairedDevices >>>>>>>>>>>>>>>>>>>>>>>>>>>");
        availableDevices = BluetoothModule.getInstance().getNonPairedDevices(context);

        checkedAvailableDevices = new ArrayList<>();
        checkedSelectedDevices = new ArrayList<>();
    }

    public void findDevices(){
        if (mode == Mode.noChecking){
            devicesListener.onDevicesFound(selectedDevices, availableDevices);
            return;
        }
        else if (mode == Mode.checkOnlySelected){
            if (getNextSelected() == null){
                devicesListener.onDevicesFound(new ArrayList<BluetoothDevice>(), availableDevices);
            }
            else {
                checkConnection(getNextSelected(), selectedListener);
            }
        }else if (mode == Mode.checkAll){
            if (getNextSelected() == null && getAvailable() == null){
                devicesListener.onDevicesFound(new ArrayList<BluetoothDevice>(), new ArrayList<BluetoothDevice>());
                return;
            }

            if (getNextSelected() == null){
                checkConnection(getAvailable(), availableListener);
            }else{
                checkConnection(getNextSelected(), selectedListener);
            }
        }
    }

    private void checkConnection(BluetoothDevice bluetoothDevice, BluetoothConnectionListener listener){
        BluetoothModule.getInstance().openSession(bluetoothDevice.getAddress(),listener);
    }

    private BluetoothConnectionListener selectedListener = new BluetoothConnectionListener() {
        @Override
        public void onConnected() {
            checkedSelectedDevices.add(getNextSelected());
            indexPaired++;
            BluetoothModule.getInstance().closeSession();

            if (getNextSelected() == null){
                if (mode == Mode.checkOnlySelected){
                    finish();
                }else if (mode == Mode.checkAll){
                    startAvailable();
                }
            }else {
                checkConnection(getNextSelected(), this);
            }
        }

        @Override
        public void onDisconnected() {
            indexPaired++;
            if (getNextSelected() == null){
                if (mode == Mode.checkOnlySelected){
                    finish();
                }else if (mode == Mode.checkAll){
                    startAvailable();
                }
            }
        }

        @Override
        public void onConnectionAttemptFailed() {
            onDisconnected();
        }

        private void startAvailable(){
            if (getAvailable() == null){
                devicesListener.onDevicesFound(checkedSelectedDevices, checkedAvailableDevices);
            }
            else{
                checkConnection(getAvailable(), availableListener);
            }
        }

        private void finish(){
            devicesListener.onDevicesFound(checkedSelectedDevices, availableDevices);
        }
    };

    private BluetoothConnectionListener availableListener = new BluetoothConnectionListener() {
        @Override
        public void onConnected() {
            BluetoothModule.getInstance().closeSession();
            checkedAvailableDevices.add(getAvailable());
            indexNonPaired++;
            if (getAvailable() == null){
                finish();
            }else{
                checkConnection(getAvailable(), this);
            }
        }

        @Override
        public void onDisconnected() {
            indexNonPaired++;
            if (getAvailable()== null){
                finish();
            }else{
                checkConnection(getAvailable(), this);
            }
        }

        @Override
        public void onConnectionAttemptFailed() {
            onDisconnected();
        }

        private void finish(){
            devicesListener.onDevicesFound(checkedSelectedDevices, checkedAvailableDevices);
        }
    };

    private BluetoothDevice getNextSelected(){
        if (indexPaired >= selectedDevices.size()){
            return null;
        }else{
            return selectedDevices.get(indexPaired);
        }
    }

    private BluetoothDevice getAvailable(){
        if (indexNonPaired >= availableDevices.size()){
            return null;
        }else{
            return availableDevices.get(indexNonPaired);
        }
    }


    public interface DevicesListener{
        void onDevicesFound(ArrayList<BluetoothDevice> pairedDevces, ArrayList<BluetoothDevice> nonPairedDevices);
    }
}

