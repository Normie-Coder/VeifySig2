package com.iveri.miuraoldsdk.devices;

import static com.miurasystems.mpi.enums.InterfaceType.MPI;

import android.content.Context;

import com.iveri.miuraoldsdk.transaction.EmvResponder;
import com.miurasystems.mpi.MpiClient;
import com.miurasystems.mpi.api.executor.MiuraManager;
import com.miurasystems.mpi.api.listener.MiuraDefaultListener;
import com.miurasystems.mpi.api.utils.DisplayTextUtils;
import com.miurasystems.mpi.enums.InterfaceType;
import com.miurasystems.mpi.enums.ResetDeviceType;
import com.miurasystems.mpi.enums.SelectFileMode;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Response;

public class DeviceInfo {
    private static boolean configsUpdateCompleted;
    public static void onUpdateConfigsClicked(EmvResponder rsp, String currency){

        configsUpdateCompleted = false;
        //Log.d(TAG, " onUpdateConfigsClicked: >>>>>>>>>>>>>>>>>>>>>>>>>");

        try{
            com.iveri.plugin.miura.module.bluetooth.BluetoothModule.getInstance().openSessionDefaultDevice(new com.iveri.plugin.miura.module.bluetooth.BluetoothConnectionListener() {
                @Override
                public void onConnected() {
                    MiuraManager.getInstance().clearDeviceMemory(new MiuraDefaultListener() {

                        @Override
                        public void onSuccess() {
                            MiuraManager.getInstance().executeAsync(client -> {

                              //  doFileUploads(client,rsp, currency);

                                try {
                                    doFileUploads(client,rsp, currency);

                                } catch (IOException e) {
                                    //e.printStackTrace();
                                    errorHandler(-472, "getting mpi infomation failed IOxception", e);
                                } catch (com.iveri.plugin.miura.exception.mPressException e) {
                                    errorHandler(-472, "getting mpi infomation failed mPressException", e);;
                                }


                            });
                        }

                        @Override
                        public void onError() {
                            // Log.d(TAG, "onError: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            errorHandler(-473, "Hard reset failed", new com.iveri.plugin.miura.exception.mPressException("clearDeviceMemory"));

                        }


                    });
                }

                @Override
                public void onDisconnected() {
                    // Log.d(TAG, " onDisconnected() : >>>>>>>>>>>>>>>>>>>>>>>>>>");
                    if (!configsUpdateCompleted ){
                       // errorHandler(-427, "Device disconnected",new mPressException("openSessionDefaultDevice disconnected"));
                    }

                }

                @Override
                public void onConnectionAttemptFailed() {
                    if (rsp.incrementCounter()>=5){
                        onDisconnected();
                    }
                    else{
                        DeviceInfo.onUpdateConfigsClicked(rsp, currency);
                    }
                    // onDisconnected();
                }


            });
        }
        catch (Error e){
           // errorHandler(-447, "Download configs failed please ensure Device is connected",new mPressException(e.getMessage()));
            //responder.getCallbackContext().error(device.getName()+ " disconnected please retry again");
        }

    }
/*
    public static void onConfirmUpdateConfigsClicked(EmvResponder responder) throws Exception {
        Response response = null;
        try {

            response = ConnectionUtil.doOkHttpsConnection(appstoreObj.getString("appStoreConfirmUrl")+"?upgradeId="+savedUpgradeId,"GET",savedAppStoreToken, "");
            responder.getCallbackContext().success("done");
        } catch (JSONException | IOException e) {
            // e.printStackTrace();
            errorHandler(-478, e.getMessage(),e);


        }
        //Log.d(TAG, response.toString()+" after saveConfigs >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

 */

    public static void doFileUploads(MpiClient client, EmvResponder rsp, String currency) throws IOException, com.iveri.plugin.miura.exception.mPressException {
        InterfaceType interfaceType = MPI;


        boolean ok = client.displayText(MPI, DisplayTextUtils.getCenteredText("Updating....\nConfig files..."),
                true, true, true);
        //Log.d(TAG, currency+" doFileUploads: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        //Log.d(TAG, mpiVersion+" doManualFileUploads: mpiVersion >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        ArrayList<String> configArray = new ArrayList<String>();

        configArray.add("AACDOL.CFG");
        configArray.add("ARQCDOL.CFG");
        configArray.add("capkeys.cfg");
        configArray.add("capkeys.cfg.sig");
        //configArray.add("contactless.cfg");
        //configArray.add("ctls-prompts.txt");
        configArray.add("emv.cfg");
        configArray.add("idle-screen-nb-pp-sa.bmp");
        configArray.add("MPI-idle.png");

        //configArray.add("emv.cfg");
        //configArray.add("OPDOL.CFG");
        configArray.add("MPI-Dynamic.cfg");
        configArray.add("P2PEDOL.CFG");
        configArray.add("prompts.txt");
        configArray.add("TCDOL.CFG");
        configArray.add("TDOL.CFG");
        configArray.add("TRMDOL.CFG");


        //  Log.d(TAG, "doFileUploads: whaaaaaaat >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        for (String filename : configArray) {
            String path = rsp.getContext().getFilesDir().getAbsolutePath()+"/mpi_config/SHUTTLEUPDATE/" + filename;
      /*
      if (currency.equalsIgnoreCase("ZAR")){
        path = rsp.getContext().getFilesDir().getAbsolutePath()+"/south-africa/mpi_config/SHUTTLEUPDATE/" + filename;
      }
      else{
        path = rsp.getContext().getFilesDir().getAbsolutePath()+"/namibia/mpi_config/SHUTTLEUPDATE/" + filename;
      }

       */

            //Log.d(TAG, path+" path : >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            File fileName = new File(path);
            if (fileName.exists()){
                //Log.d(TAG, path+ " doFileUploads: fileName.exists() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                //String path = rsp.getContext().getFilesDir().getAbsolutePath()+"/mpi_config/SHUTTLEUPDATE/" + filename;
                InputStream inputStream = new FileInputStream(path);
                // File fileName = new File(path);


                int size = inputStream.available();
                final byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();

                int pedFileSize = client.selectFile(interfaceType, SelectFileMode.Truncate, filename);

                //noinspection SimplifiableIfStatement
                if (pedFileSize < 0) {
                    //showBadFileUploadMessage(filename);
                    //responder.getCallbackContext().error("pedFileSize < 0");
                    //return;
                    throw new com.iveri.plugin.miura.exception.mPressException( "pedFileSize < 0");
                }
                ok = client.streamBinary(
                        interfaceType, buffer, 0, 0, buffer.length, 100);
                if (!ok) {

                    throw new com.iveri.plugin.miura.exception.mPressException( "!ok");
                }
            }
        }





        ArrayList<String> configContactlessArray = new ArrayList<String>();
        configContactlessArray.add("contactless.cfg");
        configContactlessArray.add("ctls-prompts.txt");
        configContactlessArray.add("P2PEDOL.CFG");
        configContactlessArray.add("TCDOL.CFG");

        for (String filename : configContactlessArray) {
            String path =  rsp.getContext().getFilesDir().getAbsolutePath()+"/mpi_config/CONTACTLESS/" + filename;;
      /*
      if (currency.equalsIgnoreCase("ZAR")){
        path = rsp.getContext().getFilesDir().getAbsolutePath()+"/south-africa/mpi_config/CONTACTLESS/" + filename;
      }
      else{
        path = rsp.getContext().getFilesDir().getAbsolutePath()+"/namibia/mpi_config/CONTACTLESS/" + filename;
      }

       */
            // String path = rsp.getContext().getFilesDir().getAbsolutePath()+"/mpi_config/CONTACTLESS/" + filename;
            //Log.d(TAG, path+" path : >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            File fileName = new File(path);
            if (fileName.exists()){
                InputStream inputStream = new FileInputStream(path);
                int size = inputStream.available();
                final byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                //Log.d(TAG, " doFileUploads: inputStream.close()>>>>>>>>>>>>>>>>>>>>>>>>>>");

                int pedFileSize = client.selectFile(interfaceType, SelectFileMode.Truncate, filename);

                //noinspection SimplifiableIfStatement
                if (pedFileSize < 0) {
                    //responder.getCallbackContext().error("pedFileSize < 0");
                    throw new com.iveri.plugin.miura.exception.mPressException( "pedFileSize < 0");
                    //return;
                }
                ok = client.streamBinary(
                        interfaceType, buffer, 0, 0, buffer.length, 100);
                if (!ok) {

                    //responder.getCallbackContext().error("!ok");
                    //LOGGER.error( "Error Config-file");
                    // client.closeSession();
                    throw new com.iveri.plugin.miura.exception.mPressException( "!ok");
                }
            }else{
                throw new com.iveri.plugin.miura.exception.mPressException("Filename "+fileName+" does not exist");
            }

        }

        //Log.d(TAG, " configsUpdateCompleted doFileUploads: >>>>>>>>>>>>>>>>>>>>>>>>>>");
        configsUpdateCompleted = true;
        //responder.getCallbackContext().success("done");
        client.resetDevice(interfaceType, ResetDeviceType.Hard_Reset);


    }

    public static void errorHandler(int responseCode, String description, Exception ex) throws Exception {
        throw  ex;
    }
}
