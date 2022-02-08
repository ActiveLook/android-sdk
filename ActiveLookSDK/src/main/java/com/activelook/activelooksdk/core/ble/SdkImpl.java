/*

Copyright 2021 Microoled
Licensed under the Apache License, Version 2.0 (the “License”);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an “AS IS” BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.activelook.activelooksdk.core.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.widget.Toast;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import com.activelook.activelooksdk.DiscoveredGlasses;
import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.Sdk;
import com.activelook.activelooksdk.exceptions.UnsupportedBleException;
import com.activelook.activelooksdk.types.GlassesUpdate;

import java.util.HashMap;

class SdkImpl implements Sdk {

    private final Context context;
    private final GlassesUpdater updater;
    private final BluetoothManager manager;
    private final BluetoothAdapter adapter;
    private final BluetoothLeScanner scanner;
    private final HashMap<String, GlassesImpl> connectedGlasses = new HashMap<>();
    private ScanCallback scanCallback;

    SdkImpl(Context context,
            String token,
            Consumer<GlassesUpdate> onUpdateStart,
            Consumer<GlassesUpdate> onUpdateProgress,
            Consumer<GlassesUpdate> onUpdateSuccess,
            Consumer<GlassesUpdate> onUpdateError) throws UnsupportedBleException {
        this.context = context;
        this.updater = new GlassesUpdater(
                context,
                token,
                onUpdateStart,
                onUpdateProgress,
                onUpdateSuccess,
                onUpdateError);
        this.manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.adapter = this.manager.getAdapter();
        if (this.adapter == null) {
            throw new UnsupportedBleException();
        }
        this.scanner = this.adapter.getBluetoothLeScanner();
    }

    Context getContext() {
        return this.context;
    }

    private void toast(String text) {
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startScan(Consumer<DiscoveredGlasses> onDiscoverGlasses) {
        this.scanCallback = new ScanCallbackImpl(onDiscoverGlasses);
        this.scanner.startScan(this.scanCallback);
    }

    @Override
    public void stopScan() {
        this.scanner.stopScan(this.scanCallback);
        this.scanCallback = null;
    }

    @Override
    public boolean isScanning() {
        return this.scanCallback != null;
    }

    void registerConnectedGlasses(GlassesImpl bleGlasses) {
        this.connectedGlasses.put(bleGlasses.getAddress(), bleGlasses);
    }

    void unregisterConnectedGlasses(GlassesImpl bleGlasses) {
        this.connectedGlasses.remove(bleGlasses.getAddress());
    }

    GlassesImpl getConnectedBleGlasses(final String address) {
        return this.connectedGlasses.get(address);
    }

    void update(final DiscoveredGlasses discoveredGlasses, final GlassesImpl glasses, final Consumer<Glasses> onConnected) {
        this.updater.update(discoveredGlasses, glasses, onConnected);
    }

    LiveData<String> getLogs() {
        return null;
    }

}
