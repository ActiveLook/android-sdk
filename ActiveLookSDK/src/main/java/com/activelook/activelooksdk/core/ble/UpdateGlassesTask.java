package com.activelook.activelooksdk.core.ble;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.DiscoveredGlasses;
import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.DeviceInformation;
import com.activelook.activelooksdk.types.GlassesUpdate;
import com.activelook.activelooksdk.types.GlassesVersion;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

class UpdateGlassesTask {

    static final String BASE_URL = "http://vps468290.ovh.net:8000";
    static final String FW_CHANNEL = "BETA";
    static final int FW_COMPAT = 4;

    private final RequestQueue requestQueue;
    private final Glasses glasses;
    private final DiscoveredGlasses discoveredGlasses;
    private final GlassesVersion gVersion;
    private UpdateProgress progress;

    private final Consumer<GlassesUpdate> onUpdateStartCallback;
    private final Consumer<GlassesUpdate> onUpdateProgressCallback;
    private final Consumer<GlassesUpdate> onUpdateSuccessCallback;
    private final Consumer<GlassesUpdate> onUpdateErrorCallback;

    private void onUpdateStart(final UpdateProgress progress) {
        this.progress = progress.withProgress(0);
        this.onUpdateStartCallback.accept(this.progress);
    }
    private void onUpdateProgress(final UpdateProgress progress) {
        this.progress = progress;
        this.onUpdateProgressCallback.accept(this.progress);
    }
    private void onUpdateSuccess(final UpdateProgress progress) {
        this.progress = progress.withProgress(100);
        this.onUpdateSuccessCallback.accept(this.progress);
    }
    private void onUpdateError(final UpdateProgress progress) {
        this.progress = progress;
        this.onUpdateErrorCallback.accept(this.progress);
    }

    UpdateGlassesTask(
            final RequestQueue requestQueue,
            final DiscoveredGlasses discoveredGlasses,
            final Glasses glasses,
            final Consumer<GlassesUpdate> onUpdateStart,
            final Consumer<GlassesUpdate> onUpdateProgress,
            final Consumer<GlassesUpdate> onUpdateSuccess,
            final Consumer<GlassesUpdate> onUpdateError) {
        this.requestQueue = requestQueue;
        this.discoveredGlasses = discoveredGlasses;
        this.glasses = glasses;
        this.onUpdateStartCallback = onUpdateStart;
        this.onUpdateProgressCallback = onUpdateProgress;
        this.onUpdateSuccessCallback = onUpdateSuccess;
        this.onUpdateErrorCallback = onUpdateError;

        final DeviceInformation gInfo = glasses.getDeviceInformation();
        this.gVersion = new GlassesVersion(gInfo.getFirmwareVersion());

        @SuppressLint("DefaultLocale")
        final String strVersion = String.format("%d.%d.%d",this.gVersion.getMajor(), this.gVersion.getMinor(), this.gVersion.getPatch());

        this.progress = new UpdateProgress(discoveredGlasses, GlassesUpdate.State.DOWNLOADING_FIRMWARE, 0,
                strVersion, "", "", "");

        Log.d("UPDATE", String.format("Create update task for: %s", gInfo));

        @SuppressLint("DefaultLocale")
        final String fwHistoryURL = String.format("%s/firmwares/%s/%s?compatibility=%d&min-version=%s",
                BASE_URL, gInfo.getHardwareVersion(), FW_CHANNEL, FW_COMPAT, strVersion);

        requestQueue.add(new JsonObjectRequest(
                Request.Method.GET,
                fwHistoryURL,
                null,
                this::onFirmwareHistoryResponse,
                this::onApiFail
        ));
    }

    private void onApiFail(final Exception error) {
        error.printStackTrace();
    }

    void onFirmwareHistoryResponse(final JSONObject jsonObject) {
        Log.d("UPDATE", String.format("Received firmware history: %s", jsonObject));
    }

}
