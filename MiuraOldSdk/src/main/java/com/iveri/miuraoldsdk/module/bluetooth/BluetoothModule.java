package com.iveri.plugin.miura.module.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;



import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;

import com.iveri.plugin.miura.exception.mPressException;
import com.miurasystems.mpi.api.executor.MiuraManager;
import com.miurasystems.mpi.events.ConnectionInfo;
import com.miurasystems.mpi.events.MpiEventHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BluetoothModule {

    private BluetoothDevice defaultBluetoothDevice;
    private static BluetoothModule instance;


    private MyHandler mHandler;

    private final MpiEventHandler<ConnectionInfo> mConnectEventHandler =
            new MpiEventHandler<ConnectionInfo>() {
                @Override
                public void handle( ConnectionInfo arg) {
                    MiuraManager.getInstance().getMpiEvents().Connected.deregister(this);
                    if (mHandler == null) throw new AssertionError();
                    Message message = mHandler.obtainMessage(MyHandler.BLUETOOTH_CONNECTED);
                    message.sendToTarget();
                }
            };

    private final MpiEventHandler<ConnectionInfo> mDisconnectEventHandler =
            new MpiEventHandler<ConnectionInfo>() {
                @Override
                public void handle( ConnectionInfo arg) {
                    MiuraManager.getInstance().getMpiEvents().Disconnected.deregister(this);

                    if (mHandler == null){
                        return;
                    }

                    Message message = mHandler.obtainMessage(MyHandler.BLUETOOTH_DISCONNECTED);
                    message.sendToTarget();
                }
            };
    private final AtomicBoolean mSessionOpened = new AtomicBoolean(false);

    private BluetoothModule(){

    }

    public static BluetoothModule getInstance(){
        if  (instance == null){
            instance = new BluetoothModule();
        }

        return instance;
    }

    public ArrayList<BluetoothDevice> getPairedDevices(Context context) throws mPressException {
        BluetoothPairing bluetoothPairing = new BluetoothPairing(context);
        return bluetoothPairing.getPairedDevices();
    }

    public ArrayList<BluetoothDevice> getNonPairedDevices(Context context) throws mPressException{
        BluetoothPairing bluetoothPairing = new BluetoothPairing(context);
        return bluetoothPairing.getNonPairedDevices();
    }

    public void openSessionDefaultDevice(final BluetoothConnectionListener connectionListener) throws IllegalStateException{
        if (defaultBluetoothDevice == null){
            throw new IllegalStateException("There is no default device, call setSelectedBluetoothDevice");
        }

        openSession(defaultBluetoothDevice.getAddress(), connectionListener);

    }

    public void openSession(String deviceAddress, BluetoothConnectionListener btConnectionListener){
        closeSession();
        mHandler = new MyHandler(btConnectionListener);
        new BluetoothAsyncConnector(deviceAddress, mHandler).execute();
    }

    private static class MyHandler extends Handler {

        public static final int BLUETOOTH_CONNECTED = 0;
        public static final int BLUETOOTH_CONNECTED_FAILED = 1;
        public static final int BLUETOOTH_DISCONNECTED = 2;


        private final AtomicBoolean mCancelled;


        private final BluetoothConnectionListener mListener;

        public MyHandler( BluetoothConnectionListener listener){
            mListener = listener;
            mCancelled = new AtomicBoolean(false);
        }

        @Override
        public void handleMessage(Message msg){

            if (mCancelled.get()){
                return;
            }

            switch (msg.what){
                case BLUETOOTH_CONNECTED:
                    mListener.onConnected();
                    break;
                case BLUETOOTH_CONNECTED_FAILED:
                    mListener.onConnectionAttemptFailed();
                    break;
                case BLUETOOTH_DISCONNECTED:
                    mListener.onDisconnected();
                    break;
            }
        }

        private void cancel(){
            mCancelled.set(true);
        }

    }

    private class BluetoothAsyncConnector extends AsyncTask<Void, Void, Void>{

        private final String mDeviceAddress;


        private final Handler mHandler;

        public BluetoothAsyncConnector( String deviceAddress,
                                        Handler handler){
            mDeviceAddress = deviceAddress;
            mHandler = handler;

            MiuraManager.getInstance().getMpiEvents().Connected.register(mConnectEventHandler);
            MiuraManager.getInstance().getMpiEvents().Disconnected.register(mDisconnectEventHandler);

        }


        @Override
        protected Void doInBackground(Void... params) {
            BluetoothDevice device = null;
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

            for (BluetoothDevice possibleDevice : pairedDevices){
                if (possibleDevice.getAddress().equals(mDeviceAddress)){
                    device = possibleDevice;
                    break;
                }
            }

            if (device == null){
                Message message = mHandler.obtainMessage(MyHandler.BLUETOOTH_CONNECTED_FAILED);
                message.sendToTarget();
                return null;
            }
            AndroidBluetoothClientConnector connector = new AndroidBluetoothClientConnector(device);

            MiuraManager.getInstance().setConnector(connector);

            try {
                MiuraManager.getInstance().openSession();
                if (!BluetoothModule.this.mSessionOpened.compareAndSet(false,true)){

                }
            }
            catch (IOException exc){
                Message message = mHandler.obtainMessage(MyHandler.BLUETOOTH_CONNECTED_FAILED);
                message.sendToTarget();
                if (BluetoothModule.this.mSessionOpened.get()){

                }
            }

            return null;
        }
    }

    public void setTimeoutEnable(boolean enable){
        if (enable){

        }
        else{

        }
    }

    public void closeSession() {
      try{
        if (mHandler != null){
          mHandler.cancel();
          mHandler = null;
        }

        MiuraManager.getInstance().getMpiEvents().Connected.deregister(mConnectEventHandler);
        MiuraManager.getInstance().getMpiEvents().Disconnected.deregister(mDisconnectEventHandler);
        mSessionOpened.set(false);
        MiuraManager.getInstance().closeSession();

      }
      catch (Exception e){
        e.printStackTrace();
      }

    }

    public boolean isSessionOpen(){
        return mSessionOpened.get();
    }

    public void setSelectedBluetoothDevice(BluetoothDevice defaultBluetoothDevice){
        this.defaultBluetoothDevice = defaultBluetoothDevice;
    }

	public BluetoothDevice getSelectedBluetoothDevice() {
        return defaultBluetoothDevice;
    }


    public BluetoothDevice getDefaultBluetoothDevice(){
        return defaultBluetoothDevice;
    }

    public void setDefaultDevice(Context context, BluetoothDevice bluetoothDevice){
        BluetoothDeviceType type = BluetoothDeviceType.getByDeviceTypeByName(bluetoothDevice.getName());
        BluetoothPairing.setDefaultDevice(context, type, bluetoothDevice.getAddress());
    }

    public void unsetDefaultDevice(Context context, BluetoothDevice bluetoothDevice){
        BluetoothDeviceType type = BluetoothDeviceType.getByDeviceTypeByName(bluetoothDevice.getName());
        BluetoothPairing.setDefaultDevice(context, type, null);
    }

    public boolean isDefaultDevice(Context context, BluetoothDevice bluetoothDevice){
        BluetoothDeviceType type = BluetoothDeviceType.getByDeviceTypeByName(bluetoothDevice.getName());
        String defaultAddress = BluetoothPairing.getDefaultDeviceAddress(context, type);
        return bluetoothDevice.getAddress().equals(defaultAddress);
    }

    public BluetoothDevice getDefaultSelectedDevice(Context context, BluetoothDeviceType deviceType) throws mPressException {
        BluetoothPairing bluetoothPairing = new BluetoothPairing(context);
        return bluetoothPairing.getDefaultByType(deviceType);
    }

    public void getBluetoothDevicesWithChecking(Context context, BluetoothDeviceChecking.Mode mode, BluetoothDeviceChecking.DevicesListener listener) throws mPressException{
      //Log.d("testing", "getBluetoothDevicesWithChecking: before BluetoothDeviceChecking >>>>>>>>>>>>>>>>>>>>");
        BluetoothDeviceChecking checks = new BluetoothDeviceChecking(context, mode, listener);
     // Log.d("testing", "getBluetoothDevicesWithChecking: before findDevices >>>>>>>>>>>>>>>>>>>>>>>.");
        checks.findDevices();
     // Log.d("testing", "getBluetoothDevicesWithChecking: after findDevices >>>>>>>>>>>>>>>>>>>>>>>.");
    }


}

