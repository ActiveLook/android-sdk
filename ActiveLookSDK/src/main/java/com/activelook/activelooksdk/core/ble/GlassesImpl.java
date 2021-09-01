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

import android.os.Parcel;
import android.util.Log;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.DiscoveredGlasses;
import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.core.GlassesCommandsAdapter;
import com.activelook.activelooksdk.core.Payload;
import com.activelook.activelooksdk.types.DeviceInformation;
import com.activelook.activelooksdk.types.FlowControlStatus;

class GlassesImpl extends GlassesCommandsAdapter implements Glasses {

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
    private DiscoveredGlassesImpl connectedFrom;

    GlassesImpl(DiscoveredGlassesImpl discoveredGlasses, Consumer<Glasses> onConnected,
                Consumer<DiscoveredGlasses> onConnectionFail,
                Consumer<Glasses> onDisconnected) {
        super();
        this.connectedFrom = discoveredGlasses;
        this.gattCallbacks = new GlassesGattCallbackImpl(
                this.connectedFrom.scanResult.getDevice(),
                this,
                onConnected,
                () -> onConnectionFail.accept(discoveredGlasses),
                onDisconnected);
    }

    protected GlassesImpl(Parcel in) {
        this.connectedFrom = in.readParcelable(DiscoveredGlassesImpl.class.getClassLoader());
        final SdkImpl sdk = BleSdkSingleton.getInstance();
        GlassesImpl registered = sdk.getConnectedBleGlasses(this.getAddress());
        this.gattCallbacks = registered.gattCallbacks;
        this.gattCallbacks.updateRef(this);
    }

    @Override
    public void writeCommand(byte[] payload) {
        Log.w("writeCommand", Payload.bytesToStr(payload));
        Log.w("writeCommand", String.format("payload length: %d", payload.length));
        this.gattCallbacks.writeCommand(payload);
    }

    @Override
    public String getManufacturer() {
        return this.connectedFrom.getManufacturer();
    }

    @Override
    public String getName() {
        return this.connectedFrom.getName();
    }

    @Override
    public String getAddress() {
        return this.connectedFrom.getAddress();
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

    void callCallback(Payload payload) {
        this.delegateToCallback(payload);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.connectedFrom, flags);
    }

    public void readFromParcel(Parcel source) {
        this.connectedFrom = source.readParcelable(DiscoveredGlassesImpl.class.getClassLoader());
    }

}
