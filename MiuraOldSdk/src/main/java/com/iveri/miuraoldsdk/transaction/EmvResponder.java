package com.iveri.miuraoldsdk.transaction;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

//import org.apache.cordova.CallbackContext;
import org.json.JSONObject;

public class EmvResponder  {
    private String tagJsonString = null;
	private boolean bluetoothConnected = false;
	private  Context context ;
	private  int counter = 0;
  private String deviceData;

  /*
   CallbackContext callbackContext;
    public EmvResponder(CallbackContext callbackContext){
        this.callbackContext = callbackContext;

    }

   */

    public EmvResponder( Context context){
     // this.callbackContext = callbackContext;
      this.context = context;
    }
/*
    public CallbackContext getCallbackContext(){
      return callbackContext;
    }

 */

    public String getTagJsonObj() {
        return tagJsonString;
    }

    public void setTagJsonObj(String tagJsonObj){
        this.tagJsonString = tagJsonString;
    }

	 public void setBluetoothConnected(boolean bluetoothConnected){
        this.bluetoothConnected = bluetoothConnected;
    }

    public boolean getBluetoothConnected(){
        return this.bluetoothConnected;
    }

	public  int incrementCounter(){
        ++counter;
        return counter;
    }

    public Context getContext(){
        return context;
    }



  public String getDeviceData() {
    return deviceData;
  }

  public void setDeviceData(String deviceData) {
    this.deviceData = deviceData;
  }
}
