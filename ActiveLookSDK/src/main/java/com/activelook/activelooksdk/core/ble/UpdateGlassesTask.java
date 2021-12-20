package com.activelook.activelooksdk.core.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    private int suotaVersion;
    private int suotaPatchDataSize;
    private int suotaMtu;
    private int suotaL2capPsm;

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

    private void onBluetoothError() {
    }

    private void onCharacteristicError(final BluetoothGattCharacteristic characteristic) {
    }

    void onFirmwareHistoryResponse(final JSONObject jsonObject) {
        try {
            final JSONObject latest = jsonObject.getJSONObject("latest");
            Log.d("FW_LATEST", String.format("%s", latest));
            final JSONArray lVersion = latest.getJSONArray("version");
            final int major = lVersion.getInt(0);
            final int minor = lVersion.getInt(1);
            final int patch = lVersion.getInt(2);
            this.progress = this.progress.withTargetFirmwareVersion(String.format("%d.%d.%d", major, minor, patch));
            if (
                    major > this.gVersion.getMajor()
                            || (major == this.gVersion.getMajor() && minor >  this.gVersion.getMinor())
                            || (major == this.gVersion.getMajor() && minor == this.gVersion.getMinor() && patch > this.gVersion.getPatch())
            ) {
                this.onUpdateStart(this.progress.withStatus(GlassesUpdate.State.DOWNLOADING_FIRMWARE));
                this.requestQueue.add(new FileRequest(
                        String.format("%s%s", BASE_URL, latest.getString("api_path")),
                        this::onFirmwareDownloaded,
                        this::onApiFail
                ));
            } else {
                Log.d("FW_LATEST", String.format("No firmware update available"));
            }
        } catch (final JSONException e) {
            this.onApiFail(e);
        }
    }

    private void onFirmwareDownloaded(final byte[] response) {
        this.onUpdateProgress(progress.withStatus(GlassesUpdate.State.UPDATING_FIRMWARE).withProgress(0));
        Log.d("FIRMWARE DOWNLOADER", String.format("bytes: [%d] %s", response.length, response));
        // TODO: start SUOTA
    }

    private void suotaUpdate(final GlassesGatt gatt) {
        this.suotaRead_SUOTA_VERSION_UUID(gatt);
    }

    private void suotaRead_SUOTA_VERSION_UUID(final GlassesGatt gatt) {
        final BluetoothGattService service = gatt.getService(GlassesGatt.SPOTA_SERVICE_UUID);
        gatt.readCharacteristic(
                service.getCharacteristic(GlassesGatt.SUOTA_VERSION_UUID),
                c -> {
                    this.suotaVersion = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    this.suotaRead_SUOTA_PATCH_DATA_CHAR_SIZE_UUID(gatt, service);
                },
                this::onCharacteristicError);
    }

    private void suotaRead_SUOTA_PATCH_DATA_CHAR_SIZE_UUID(final GlassesGatt gatt, final BluetoothGattService service) {
        gatt.readCharacteristic(
                service.getCharacteristic(GlassesGatt.SUOTA_PATCH_DATA_CHAR_SIZE_UUID),
                c -> {
                    this.suotaPatchDataSize = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                    this.suotaRead_SUOTA_MTU_UUID(gatt, service);
                },
                this::onCharacteristicError);
    }

    private void suotaRead_SUOTA_MTU_UUID(final GlassesGatt gatt, final BluetoothGattService service) {
        gatt.readCharacteristic(
                service.getCharacteristic(GlassesGatt.SUOTA_MTU_UUID),
                c -> {
                    this.suotaMtu = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                    this.suotaRead_SUOTA_L2CAP_PSM_UUID(gatt, service);
                },
                this::onCharacteristicError);
    }

    private void suotaRead_SUOTA_L2CAP_PSM_UUID(final GlassesGatt gatt, final BluetoothGattService service) {
        gatt.readCharacteristic(
                service.getCharacteristic(GlassesGatt.SUOTA_L2CAP_PSM_UUID),
                c -> {
                    this.suotaL2capPsm = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                    this.enableNotifications(gatt, service);
                },
                this::onCharacteristicError);
    }

    private void enableNotifications(final GlassesGatt gatt, final BluetoothGattService service) {
        Log.d("SUOTA", String.format("Enabling notification"));
        gatt.setCharacteristicNotification(
                service.getCharacteristic(GlassesGatt.SPOTA_SERV_STATUS_UUID),
                true,
                () -> this.setSpotaMemDev(gatt),
                this::onBluetoothError);
    }


    private void setSpotaMemDev(final GlassesGatt gatt) {
        BluetoothGattCharacteristic characteristic = gatt
                .getService(GlassesGatt.SPOTA_SERVICE_UUID)
                .getCharacteristic(GlassesGatt.SPOTA_MEM_DEV_UUID);
        final int memType = 0x13000000;
        Log.d("SPOTA", "setSpotaMemDev: " + String.format("%#010x", memType));
        characteristic.setValue(memType, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        gatt.writeCharacteristic(characteristic);
    }

}
