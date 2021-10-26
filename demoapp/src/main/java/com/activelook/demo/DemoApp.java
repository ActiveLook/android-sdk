package com.activelook.demo;

import android.app.Application;
import android.util.Log;

import com.activelook.activelooksdk.Sdk;

public class DemoApp extends Application {

    private Sdk alsdk;
    private boolean connected;

    @Override
    public void onCreate() {
        super.onCreate();
        this.connected = false;
        this.alsdk = Sdk.init(
                this.getApplicationContext(),
                "MyPrivateToken",
                (update) -> Log.i("GLASSES_UPDATE", "Starting glasses update."),
                (update) -> Log.i("GLASSES_UPDATE", "Progressing glasses update."),
                (update) -> Log.i("GLASSES_UPDATE", "Success glasses update."),
                (update) -> Log.i("GLASSES_UPDATE", "Error glasses update.")
        );
    }

    public Sdk getActiveLookSdk() {
        return this.alsdk;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void onConnected() {
        this.connected = true;
    }

    public void onDisconnected() {
        this.connected = false;
    }

}
