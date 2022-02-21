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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.util.Log;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.DiscoveredGlasses;
import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.core.AbstractGlasses;
import com.activelook.activelooksdk.core.Command;
import com.activelook.activelooksdk.types.DeviceInformation;
import com.activelook.activelooksdk.types.FlowControlStatus;

class GlassesImpl extends AbstractGlasses implements Glasses {

    public static final Creator<GlassesImpl> CREATOR = new Creator<GlassesImpl>() {
        @Override
        public GlassesImpl createFromParcel(Parcel source) {
            return new GlassesImpl(source);
        }

        @Override
        public GlassesImpl[] newArray(int size) {
            return new GlassesImpl[size];
        }
    };
    private final GlassesGattCallbackImpl gattCallbacks;

    private String manufacturer;
    private BluetoothDevice device;

    GlassesImpl(DiscoveredGlassesImpl discoveredGlasses, Consumer<Glasses> onConnected,
                Consumer<DiscoveredGlasses> onConnectionFail,
                Consumer<Glasses> onDisconnected) {
        super();
        this.manufacturer = discoveredGlasses.getManufacturer();
        this.device = discoveredGlasses.scanResult.getDevice();
        this.gattCallbacks = new GlassesGattCallbackImpl(
                device,
                this,
                onConnected,
                () -> onConnectionFail.accept(discoveredGlasses),
                onDisconnected);
    }

    GlassesImpl(String address, Consumer<Glasses> onConnected,
                Consumer<String> onConnectionFail,
                Consumer<Glasses> onDisconnected) {
        super();
        this.manufacturer = "";
        final SdkImpl sdk = BleSdkSingleton.getInstance();
        this.device = sdk.adapter.getRemoteDevice(address);
        this.gattCallbacks = new GlassesGattCallbackImpl(
                device,
                this,
                onConnected,
                () -> onConnectionFail.accept(address),
                onDisconnected);
    }

    protected GlassesImpl(Parcel in) {
        this.manufacturer = in.readString();
        this.device = in.readParcelable(BluetoothDevice.class.getClassLoader());

        final SdkImpl sdk = BleSdkSingleton.getInstance();
        GlassesImpl registered = sdk.getConnectedBleGlasses(this.getAddress());
        this.gattCallbacks = registered.gattCallbacks;
        this.gattCallbacks.updateRef(this);
    }

    @Override
    public void writeBytes(byte[] bytes) {
        Log.w("writeCommand", Command.bytesToStr(bytes));
        Log.w("writeCommand", String.format("payload length: %d", bytes.length));
        this.gattCallbacks.writeRxCharacteristic(bytes);
    }

    @Override
    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public String getName() {
        return device.getName();
    }

    @Override
    public String getAddress() {
        return device.getAddress();
    }

    @Override
    public boolean isFirmwareAtLeast(String version) {
        version = String.format("v%s.0b", version);
        final String gVersion = this.getDeviceInformation().getFirmwareVersion();
        Log.w("isFirmwareAtLeast", String.format("glasses: [%s], argument: [%s] = %d", gVersion, version, gVersion.compareTo(version)));
        return gVersion.compareTo(version) >= 0;
    }

    @Override
    public int compareFirmwareVersion(String version) {
        version = String.format("v%s.0b", version);
        final String gVersion = this.getDeviceInformation().getFirmwareVersion();
        Log.w("compareFirmwareVersion", String.format("glasses: [%s], argument: [%s] = %d", gVersion, version,
                gVersion.compareTo(version)));
        return gVersion.compareTo(version);
    }

    @Override
    public void disconnect() {
        this.gattCallbacks.disconnect();
    }

    @Override
    public void setOnDisconnected(Consumer<Glasses> onDisconnected) {
        this.gattCallbacks.setOnDisconnected(onDisconnected);
    }

    @Override
    public DeviceInformation getDeviceInformation() {
        return this.gattCallbacks.getDeviceInformation();
    }

    @Override
    public void subscribeToBatteryLevelNotifications(Consumer<Integer> onEvent) {
        this.gattCallbacks.subscribeToBatteryLevelNotifications(onEvent);
    }

    @Override
    public void subscribeToFlowControlNotifications(Consumer<FlowControlStatus> onEvent) {
        this.gattCallbacks.subscribeToFlowControlNotifications(onEvent);
    }

    @Override
    public void subscribeToSensorInterfaceNotifications(Runnable onEvent) {
        this.gattCallbacks.subscribeToSensorInterfaceNotifications(onEvent);
    }

    void callCallback(Command command) {
        this.delegateToCallback(command);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(manufacturer);
        device.writeToParcel(dest, flags);
    }

    public void readFromParcel(Parcel source) {
        this.manufacturer = source.readString();
        this.device = source.readParcelable(BluetoothDevice.class.getClassLoader());
    }

}
