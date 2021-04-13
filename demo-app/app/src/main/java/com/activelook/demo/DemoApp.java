package com.activelook.demo;

import android.app.Application;

import com.activelook.activelooksdk.Sdk;

public class DemoApp extends Application {

    private Sdk alsdk;

    @Override
    public void onCreate() {
        super.onCreate();
        this.alsdk = Sdk.init(this.getApplicationContext());
    }

    public Sdk getActiveLookSdk() {
        return this.alsdk;
    }

}
