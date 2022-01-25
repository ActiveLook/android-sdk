package com.activelook.activelooksdk.core.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import com.activelook.activelooksdk.DiscoveredGlasses;
import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.ConfigurationElementsInfo;
import com.activelook.activelooksdk.types.DeviceInformation;
import com.activelook.activelooksdk.types.GlassesUpdate;
import com.activelook.activelooksdk.types.GlassesVersion;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

class UpdateGlassesTask {

    static final String BASE_URL = "http://vps468290.ovh.net:8000";
    static final String FW_CHANNEL = "BETA";
    static final int FW_COMPAT = 4;

    static final int BLOCK_SIZE = 240;

    private final RequestQueue requestQueue;
    private final GlassesImpl glasses;
    private final DiscoveredGlasses discoveredGlasses;
    private final GlassesVersion gVersion;
    private UpdateProgress progress;

    private final Consumer<Glasses> onConnected;

    private final Consumer<GlassesUpdate> onUpdateStartCallback;
    private final Consumer<GlassesUpdate> onUpdateProgressCallback;
    private final Consumer<GlassesUpdate> onUpdateSuccessCallback;
    private final Consumer<GlassesUpdate> onUpdateErrorCallback;

    private int suotaVersion = 0;
    private int suotaPatchDataSize = 20;
    private int suotaMtu = 23;
    private int suotaL2capPsm = 0;

    private Firmware firmware;
    private List<Pair<Integer, List<byte[]>>> blocks;
    private int blockId;
    private int chunkId;
    private int patchLength;

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
            final GlassesImpl glasses,
            final Consumer<Glasses> onConnected,
            final Consumer<GlassesUpdate> onUpdateStart,
            final Consumer<GlassesUpdate> onUpdateProgress,
            final Consumer<GlassesUpdate> onUpdateSuccess,
            final Consumer<GlassesUpdate> onUpdateError) {
        this.requestQueue = requestQueue;
        this.discoveredGlasses = discoveredGlasses;
        this.glasses = glasses;
        this.onConnected = onConnected;
        this.onUpdateStartCallback = onUpdateStart;
        this.onUpdateProgressCallback = onUpdateProgress;
        this.onUpdateSuccessCallback = onUpdateSuccess;
        this.onUpdateErrorCallback = onUpdateError;

        final DeviceInformation gInfo = glasses.getDeviceInformation();
        this.gVersion = new GlassesVersion(gInfo.getFirmwareVersion());

        @SuppressLint("DefaultLocale")
        final String strVersion = String.format("%d.%d.%d", this.gVersion.getMajor(), this.gVersion.getMinor(), this.gVersion.getPatch());

        this.progress = new UpdateProgress(discoveredGlasses, GlassesUpdate.State.DOWNLOADING_FIRMWARE, 0,
                strVersion, "", "", "");

        Log.d("UPDATE", String.format("Create update task for: %s", gInfo));

        @SuppressLint("DefaultLocale")
        final String fwHistoryURL = String.format("%s/firmwares/%s/%s?compatibility=%d&min-version=%s",
                BASE_URL, "ALK01A", FW_CHANNEL, FW_COMPAT, strVersion);

        this.requestQueue.add(new JsonObjectRequest(
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
        Log.e("SUOTA", "Got onBluetoothError");
    }

    private void onCharacteristicError(final BluetoothGattCharacteristic characteristic) {
        final Object obj = characteristic == null ? "null" : characteristic.getUuid();
        Log.e("SUOTA", String.format("Got onCharacteristicError %s", obj));
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
                this.glasses.cfgRead("ALooK", info -> {
                    @SuppressLint("DefaultLocale")
                    final String strVersion = String.format("%d.%d.%d", this.gVersion.getMajor(), this.gVersion.getMinor(), this.gVersion.getPatch());

                    @SuppressLint("DefaultLocale")
                    final String cfgHistoryURL = String.format("%s/configurations/%s/%s?compatibility=%d&max-version=%s",
                            BASE_URL, "ALK01A", FW_CHANNEL, FW_COMPAT, strVersion);

                    this.requestQueue.add(new JsonObjectRequest(
                            Request.Method.GET,
                            cfgHistoryURL,
                            null,
                            r -> this.onConfigurationHistoryResponse(r, info),
                            this::onApiFail
                    ));
                });
            }
        } catch (final JSONException e) {
            this.onApiFail(e);
        }
    }

    private void onConfigurationHistoryResponse(final JSONObject jsonObject, final ConfigurationElementsInfo info) {
        try {
            final JSONObject latest = jsonObject.getJSONObject("latest");
            Log.d("CFG_LATEST", String.format("%s", latest));
            final JSONArray lVersion = latest.getJSONArray("version");
            final int version = lVersion.getInt(3);
            this.progress = this.progress.withTargetConfigurationVersion(String.format("%d", version));
            if (version > info.getVersion()) {
                this.onUpdateStart(this.progress.withStatus(GlassesUpdate.State.DOWNLOADING_CONFIGURATION));
                this.requestQueue.add(new FileRequest(
                        String.format("%s%s", BASE_URL, latest.getString("api_path")),
                        this::onConfigurationDownloaded,
                        this::onApiFail
                ));
            } else {
                Log.d("CFG_LATEST", String.format("No configuration update available"));
                this.onConnected.accept(this.glasses);
            }
        } catch (final JSONException e) {
            this.onApiFail(e);
        }
    }

    private void onConfigurationDownloaded(final byte[] response) {
        this.onUpdateProgress(this.progress.withStatus(GlassesUpdate.State.UPDATING_CONFIGURATION).withProgress(0));
        Log.d("CFG DOWNLOADER", String.format("bytes: [%d] %s", response.length, response));
        try {
            this.glasses.loadConfiguration(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response))));
            this.onConnected.accept(this.glasses);
        } catch (IOException e) {
            this.onUpdateError(this.progress.withStatus(GlassesUpdate.State.ERROR_UPDATE_FAIL));
        }
    }

    private void onFirmwareDownloaded(final byte[] response) {
        this.onUpdateProgress(progress.withStatus(GlassesUpdate.State.UPDATING_FIRMWARE).withProgress(0));
        Log.d("FIRMWARE DOWNLOADER", String.format("bytes: [%d] %s", response.length, response));
        this.firmware = new Firmware(response);
        this.suotaUpdate(this.glasses.gattCallbacks);
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
                () -> this.setSpotaMemDev(gatt, service),
                this::onBluetoothError,
                c -> this.onSuotaNotifications(gatt, service, c));
    }

    private void onSuotaNotifications(final GlassesGatt gatt, final BluetoothGattService service, final BluetoothGattCharacteristic characteristic) {
        int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        Log.d("Suota notification", String.format("SPOTA_SERV_STATUS notification: %#04x", value));
        if (value == 0x10) {
            this.setSpotaGpioMap(gatt, service);
        } else if (value == 0x02) {
            this.setPatchLength(gatt, service);
        } else {
            Log.e("Suota notification", String.format("SPOTA_SERV_STATUS notification error: %#04x", value));
        }
    }

    private void setSpotaMemDev(final GlassesGatt gatt, final BluetoothGattService service) {
        final int memType = 0x13000000;
        Log.d("SPOTA", "setSpotaMemDev: " + String.format("%#010x", memType));
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(GlassesGatt.SPOTA_MEM_DEV_UUID);
        characteristic.setValue(memType, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        gatt.writeCharacteristic(
                characteristic,
                c -> {
                    Log.d("SPOTA", "Wait for notification for setSpotaMemDev.");
                },
                this::onCharacteristicError);
    }

    private void setSpotaGpioMap(final GlassesGatt gatt, final BluetoothGattService service) {
        final int memInfoData = 0x05060300;
        Log.d("SPOTA", "setSpotaGpioMap: " + String.format("%#010x", memInfoData));
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(GlassesGatt.SPOTA_GPIO_MAP_UUID);
        characteristic.setValue(memInfoData, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        gatt.writeCharacteristic(
                characteristic,
                c -> {
                    final int chunkSize = Math.min(this.suotaPatchDataSize, this.suotaMtu - 3);
                    this.blocks = this.firmware.getSuotaBlocks(BLOCK_SIZE, chunkSize);
                    this.blockId = 0;
                    this.chunkId = 0;
                    this.patchLength = 0;
                    this.setPatchLength(gatt, service);
                },
                this::onCharacteristicError);
    }

    private void setPatchLength(final GlassesGatt gatt, final BluetoothGattService service) {
        if (this.blockId < this.blocks.size()) {
            final Pair<Integer, List<byte[]>> block = this.blocks.get(this.blockId);
            final int blockSize = block.first;
            if (blockSize != this.patchLength) {
                Log.d("SUOTA", "setPatchLength: " + blockSize + " - " + String.format("%#06x", blockSize));
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(GlassesGatt.SPOTA_PATCH_LEN_UUID);
                characteristic.setValue(blockSize, BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                gatt.writeCharacteristic(
                        characteristic,
                        c -> {
                            this.patchLength = blockSize;
                            this.sendBlock(gatt, service);
                        },
                        this::onCharacteristicError);
            } else {
                this.sendBlock(gatt, service);
            }
        } else {
            this.sendEndSignal(gatt, service);
        }
    }

    private void sendBlock(final GlassesGatt gatt, final BluetoothGattService service) {
        if (this.blockId < this.blocks.size()) {
            final Pair<Integer, List<byte[]>> block = this.blocks.get(this.blockId);
            final List<byte[]> chunks = block.second;
            if (this.chunkId < chunks.size()) {
                Log.d("SUOTA", String.format("sendBlock %d chunk %d", this.blockId, this.chunkId));
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(GlassesGatt.SPOTA_PATCH_DATA_UUID);
                characteristic.setValue(chunks.get(this.chunkId++));
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                gatt.writeCharacteristic(
                        characteristic,
                        c -> this.sendBlock(gatt, service),
                        this::onCharacteristicError);
            } else {
                this.blockId ++;
                this.chunkId = 0;
                Log.d("SPOTA", "Wait for notification for sendBlock.");
            }
        } else {
            this.sendEndSignal(gatt, service);
        }
    }

    private void sendEndSignal(final GlassesGatt gatt, final BluetoothGattService service) {
        Log.d("SUOTA", "sendEndSignal");
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(GlassesGatt.SPOTA_MEM_DEV_UUID);
        characteristic.setValue(0xfe000000, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        gatt.writeCharacteristic(
                characteristic,
                c -> this.sendRebootSignal(gatt, service),
                this::onCharacteristicError);
    }

    private void sendRebootSignal(final GlassesGatt gatt, final BluetoothGattService service) {
        Log.d("SUOTA", "sendRebootSignal");
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(GlassesGatt.SPOTA_MEM_DEV_UUID);
        characteristic.setValue(0xfd000000, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        gatt.writeCharacteristic(
                characteristic,
                c -> {
                    Log.d("SUOTA", String.format("REBOOTING"));
                },
                this::onCharacteristicError);
    }

}
