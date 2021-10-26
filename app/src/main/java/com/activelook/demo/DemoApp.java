package com.activelook.demo;

import android.app.Application;

import com.activelook.activelooksdk.Sdk;

public class DemoApp extends Application {

    private Sdk alsdk;
    private boolean connected;

    @Override
    public void onCreate() {
        super.onCreate();
        this.connected = false;
        this.alsdk = Sdk.init(this.getApplicationContext());
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
