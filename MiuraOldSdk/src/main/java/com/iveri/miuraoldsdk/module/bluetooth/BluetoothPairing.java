package com.iveri.plugin.miura.module.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
//import android.util.Log;

import com.iveri.plugin.miura.exception.mPressException;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothPairing  {
    private static final String TAG = "BluetoothPairing";
    private Context context;

    private Set<BluetoothDevice> allVisibleDevices;

    private ArrayList<BluetoothDevice> allMiuraDevices;

    private ArrayList<BluetoothDevice> pairedDevices;

    private ArrayList<BluetoothDevice> nonPairedDevices;

    public BluetoothPairing(Context context) throws mPressException {
        this.context = context;
        initAllAvailableDevices();
        initMiuraDevices(context);
        initPairedDevices();
        initNonPairedDevices();
    }

    private void initAllAvailableDevices() throws SecurityException, mPressException {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
          allVisibleDevices = btAdapter.getBondedDevices();
          for (BluetoothDevice device: allVisibleDevices){
            //Log.d(TAG, device.getName()+" initAllAvailableDevices: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
          }
        }catch (SecurityException exception){
          throw new mPressException(exception.getMessage());

        }


    }

    private void initMiuraDevices(Context context) {
        allMiuraDevices = new ArrayList<>();
        //Log.d(TAG, " initMiuraDevices: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (BluetoothDevice bluetoothDevice: allVisibleDevices){
            if (bluetoothDevice.getName().toLowerCase().contains(getBluetoothDeviceName(context).toLowerCase()) || bluetoothDevice.getName().toLowerCase().contains("Miura".toLowerCase())){
                //Log.d(TAG, bluetoothDevice.getName()+" after if initMiuraDevices: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                allMiuraDevices.add(bluetoothDevice);
            }
            /*
            for (BluetoothDeviceType deviceType: BluetoothDeviceType.values()){
                Log.d(TAG, bluetoothDevice.getName() +" initMiuraDevices: before if >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                if (bluetoothDevice.getName().toLowerCase().contains("Miura".toLowerCase())){
                    Log.d(TAG, bluetoothDevice.getName()+" initMiuraDevices: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    allMiuraDevices.add(bluetoothDevice);
                    break;
                }
            }

             */
        }
    }

    private void initPairedDevices(){
        pairedDevices = new ArrayList<>();
        for (BluetoothDeviceType deviceType : BluetoothDeviceType.values()){
            String deviceAddress = getDefaultDeviceAddress(context, deviceType);
            if (deviceAddress != null){
                BluetoothDevice device = findByAddress(deviceAddress, allMiuraDevices);
                if (device != null){
                    pairedDevices.add(device);
                }
            }
        }
    }

    private void initNonPairedDevices(){
        nonPairedDevices = new ArrayList<>();

        for (BluetoothDevice miuraDevice : allMiuraDevices){
            //Log.d(TAG, miuraDevice.getName()+" initNonPairedDevices: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
            boolean isAlreadyPaired = false;
            for (BluetoothDevice device : pairedDevices){
                if (device.getAddress().equals(miuraDevice.getAddress())){
                    //Log.d(TAG, miuraDevice.getName()+" pairedDevice: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.");
                    isAlreadyPaired = true;
                    break;
                }
            }

            if (!isAlreadyPaired){
               // Log.d(TAG, "Added initNonPairedDevices: ");
                nonPairedDevices.add(miuraDevice);
            }
        }
    }

    public static void setDefaultDevice(Context context, BluetoothDeviceType deviceType, String address){
        SharedPreferences prefs = context.getSharedPreferences("DevicePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(deviceType.getDeviceTypeName(), address);
        editor.commit();

    }

    public static String getDefaultDeviceAddress(Context context, BluetoothDeviceType deviceType){
        SharedPreferences prefs = context.getSharedPreferences("DevicePreferences", Context.MODE_PRIVATE);
        String BluetoothAddress = prefs.getString(deviceType.getDeviceTypeName(), null);

        return BluetoothAddress;
    }

    public static String getBluetoothDeviceName(Context context){
        SharedPreferences prefs = context.getSharedPreferences("DevicePreferences", Context.MODE_PRIVATE);
        return prefs.getString("DeviceMpressBluetoothName", "mPress");
    }

    public static BluetoothDevice findByAddress(String address, ArrayList<BluetoothDevice> bluetoothDevices){

        if (address == null){
            return null;
        }

        for (BluetoothDevice bluetoothDevice : bluetoothDevices){
            if (address.equals(bluetoothDevice.getAddress())){
                return bluetoothDevice;
            }
        }

        return null;
    }

    public ArrayList<BluetoothDevice> getPairedDevices(){
        return pairedDevices;
    }

    public ArrayList<BluetoothDevice> getNonPairedDevices(){
        return nonPairedDevices;
    }

    public BluetoothDevice getDefaultByType(BluetoothDeviceType type){
        String address = getDefaultDeviceAddress(context, type);
        return findByAddress(address, getPairedDevices());
    }
}
