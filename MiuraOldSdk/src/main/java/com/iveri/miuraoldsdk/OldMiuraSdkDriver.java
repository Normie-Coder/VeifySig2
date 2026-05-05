package com.iveri.miuraoldsdk;

import android.content.Context;

import com.iveri.miuraoldsdk.devices.DeviceInfo;
import com.iveri.miuraoldsdk.transaction.EmvResponder;

public class OldMiuraSdkDriver {

    private void onConfigUploads(String currency, Context context){
        //EmvResponder responder = new EmvResponder(callbackContext, this.cordova.getActivity());
        //Log.d(TAG, " onConfigUploads: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        EmvResponder rsp = new EmvResponder(context);
        DeviceInfo.onUpdateConfigsClicked(rsp, currency);
    }

    /*
    private void onConfirmConfigs(Context context){
        // responder = new EmvResponder(callbackContext, Context context);
        EmvResponder rsp = new EmvResponder(context);
        DeviceInfo.onConfirmUpdateConfigsClicked(rsp);
    }

     */
}
