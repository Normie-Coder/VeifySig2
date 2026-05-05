package com.iveri.plugin.miura.core;





import com.iveri.plugin.miura.util.CurrencyCode;
import com.miurasystems.mpi.api.executor.MiuraManager;

public class MiuraApplication {
    public static CurrencyCode currencyCode = CurrencyCode.ZAR;

    public void onCreate(){
        MiuraManager instance = MiuraManager.getInstance();
        instance.setDeviceType(MiuraManager.DeviceType.PED);
    }
}
