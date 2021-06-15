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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.core.Payload;
import com.activelook.activelooksdk.types.DeviceInformation;
import com.activelook.activelooksdk.types.FlowControlStatus;
import com.activelook.activelooksdk.types.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

class GlassesGattCallbackImpl extends BluetoothGattCallback {

    private final BluetoothDevice device;
    private final DeviceInformation deviceInfo;
    private final ConcurrentLinkedQueue<byte []> pendingWriteCommand;
    private final BluetoothGatt gatt;
    private int mtu;
    private GlassesImpl glasses;
    private Consumer<Glasses> onConnected;
    private Consumer<Glasses> onDisconnected;
    private byte[] pendingBuffer;
    private Consumer<Integer> onBatteryLevelEvent;
    private Consumer<FlowControlStatus> onFlowControlEvent;
    private Runnable onSensorInterfaceEvent;

    GlassesGattCallbackImpl(BluetoothDevice device, GlassesImpl bleGlasses) {
        super();
        this.device = device;
        this.deviceInfo = new DeviceInformation();
        this.pendingWriteCommand = new ConcurrentLinkedQueue<>();
        this.mtu = 20;
        this.glasses = bleGlasses;
        this.onBatteryLevelEvent = null;
        this.onFlowControlEvent = null;
        this.onSensorInterfaceEvent = null;
        final SdkImpl sdk = BleSdkSingleton.getInstance();
        this.gatt = this.device.connectGatt(sdk.getContext(), true, this);
        sdk.registerConnectedGlasses(this.glasses);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            int rmtu = 512;
            while (!this.gatt.requestMtu(rmtu)) rmtu --;
        } else {
            BleSdkSingleton.getInstance().unregisterConnectedGlasses(this.glasses);
            if (this.onDisconnected != null) {
                this.onDisconnected.accept(this.glasses);
            }
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            this.mtu = mtu;
            Log.i("MTU", String.format("MTU=%d, status=%d", mtu, status));
            this.gatt.discoverServices();
        } else {
            Log.e("MTU", String.format("MTU=%d, status=%d", mtu, status));
            this.gatt.requestMtu(mtu);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        final BluetoothGattService diService = this.gatt.getService(BleUUID.DeviceInformationService);
        if (characteristic.getUuid().equals(BleUUID.ManufacturerNameCharacteristic)) {
            this.deviceInfo.setManufacturerName(
                    new String(characteristic.getValue(), StandardCharsets.UTF_8));
            this.gatt.readCharacteristic(diService.getCharacteristic(BleUUID.ModelNumberCharacteristic));
        } else if (characteristic.getUuid().equals(BleUUID.ModelNumberCharacteristic)) {
            this.deviceInfo.setModelNumber(
                    new String(characteristic.getValue(), StandardCharsets.UTF_8));
            this.gatt.readCharacteristic(diService.getCharacteristic(BleUUID.SerialNumberCharacteristic));
        } else if (characteristic.getUuid().equals(BleUUID.SerialNumberCharacteristic)) {
            this.deviceInfo.setSerialNumber(
                    new String(characteristic.getValue(), StandardCharsets.UTF_8));
            this.gatt.readCharacteristic(diService.getCharacteristic(BleUUID.HardwareVersionCharacteristic));
        } else if (characteristic.getUuid().equals(BleUUID.HardwareVersionCharacteristic)) {
            this.deviceInfo.setHardwareVersion(
                    new String(characteristic.getValue(), StandardCharsets.UTF_8));
            this.gatt.readCharacteristic(diService.getCharacteristic(BleUUID.FirmwareVersionCharacteristic));
        } else if (characteristic.getUuid().equals(BleUUID.FirmwareVersionCharacteristic)) {
            this.deviceInfo.setFirmwareVersion(
                    new String(characteristic.getValue(), StandardCharsets.UTF_8));
            this.gatt.readCharacteristic(diService.getCharacteristic(BleUUID.SoftwareVersionCharacteristic));
        } else if (characteristic.getUuid().equals(BleUUID.SoftwareVersionCharacteristic)) {
            this.deviceInfo.setSoftwareVersion(
                    new String(characteristic.getValue(), StandardCharsets.UTF_8));
            if (this.onConnected != null) {
                this.onConnected.accept(this.glasses);
                Log.e("onDescriptorWrite", "DONE");
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (characteristic.equals(this.getRxCharacteristic())) {
            this.unstackWriteCommand();
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        Log.e("onCharacteristicChanged", characteristic.getUuid().toString());
        if (characteristic.getUuid().equals(BleUUID.ActiveLookTxCharacteristic)) {
            byte[] buffer = characteristic.getValue();
            if (this.pendingBuffer != null) {
                this.addPendingBuffer(buffer);
                buffer = this.pendingBuffer;
                if (Payload.isValidBuffer(buffer)) {
                    this.pendingBuffer = null;
                    final Payload payload = new Payload(buffer);
                    Log.w("onTXChanged Buffered", payload.toString());
                    this.glasses.callCallback(payload);
                }
            } else if (Payload.isValidBuffer(buffer)) {
                final Payload payload = new Payload(buffer);
                Log.w("onTXChanged", payload.toString());
                this.glasses.callCallback(payload);
            } else {
                this.addPendingBuffer(buffer);
            }
        } else if (characteristic.getUuid().equals(BleUUID.BatteryLevelCharacteristic)) {
            if (this.onBatteryLevelEvent != null) {
                this.onBatteryLevelEvent.accept((int) characteristic.getValue()[0]);
            }
        } else if (characteristic.getUuid().equals(BleUUID.ActiveLookSensorInterfaceCharacteristic)) {
            if (this.onSensorInterfaceEvent != null) {
                this.onSensorInterfaceEvent.run();
            }
        } else if (characteristic.getUuid().equals(BleUUID.ActiveLookFlowControlCharacteristic)) {
            if (this.onFlowControlEvent != null) {
                final byte state = characteristic.getValue()[0];
                if (state == (byte) 0x01) {
                    this.onFlowControlEvent.accept(FlowControlStatus.CAN_SEND);
                } else if (state == (byte) 0x02) {
                    this.onFlowControlEvent.accept(FlowControlStatus.STOP_SENDING);
                } else if (state == (byte) 0x03) {
                    this.onFlowControlEvent.accept(FlowControlStatus.CMD_ERROR);
                } else if (state == (byte) 0x04) {
                    this.onFlowControlEvent.accept(FlowControlStatus.OVERFLOW);
                } else if (state == (byte) 0x05) {
                    this.onFlowControlEvent.accept(FlowControlStatus.FONT_OR_BITMAP_EXPECTED);
                } else if (state == (byte) 0x06) {
                    this.onFlowControlEvent.accept(FlowControlStatus.MISSING_CONFIG_ID);
                }
            }
        } else {
            Log.e("onCharacteristicChanged", Payload.bytesToStr(characteristic.getValue()));
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        Log.e("onDescriptorWrite", descriptor.getCharacteristic().getUuid().toString());
        if (descriptor.getCharacteristic().getUuid().equals(BleUUID.ActiveLookFlowControlCharacteristic)) {
            this.activateNotification(this.getTxCharacteristic());
        } else if (descriptor.getCharacteristic().getUuid().equals(BleUUID.ActiveLookTxCharacteristic)) {
            this.activateNotification(this.getUiCharacteristic());
        } else if (descriptor.getCharacteristic().getUuid().equals(BleUUID.ActiveLookUICharacteristic)) {
            this.activateNotification(this.getSensorCharacteristic());
        } else if (descriptor.getCharacteristic().getUuid().equals(BleUUID.ActiveLookSensorInterfaceCharacteristic)) {
            final BluetoothGattService diService = this.gatt.getService(BleUUID.DeviceInformationService);
            this.gatt.readCharacteristic(diService.getCharacteristic(BleUUID.ManufacturerNameCharacteristic));
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            this.activateNotification(this.getFlowControlCharacteristic());
        }
    }

    /*
    Package protected
     */

    void updateRef(GlassesImpl bleGlasses) {
        this.glasses = bleGlasses;
    }

    void setOnDisconnected(Consumer<Glasses> onDisconnected) {
        this.onDisconnected = onDisconnected;
    }

    void setOnConnect(Consumer<Glasses> onConnected) {
        this.onConnected = onConnected;
    }

    void writeCommand(byte[] payload) {
        final byte [][] chunks = Utils.split(payload, this.mtu);
        this.pendingWriteCommand.addAll(Arrays.asList(chunks));
        this.unstackWriteCommand();
    }

    synchronized void unstackWriteCommand() {
        if (this.pendingWriteCommand.size()>0) {
            final byte [] buffer = this.pendingWriteCommand.poll();
            Log.d("unstackWriteCommand", String.format("write rx: %s", Utils.bytesToHexString(buffer)));
            this.getRxCharacteristic().setValue(buffer);
            this.gatt.writeCharacteristic(this.getRxCharacteristic());
        }
    }

    void disconnect() {
        this.gatt.disconnect();
    }

    /*
    Helpers
     */
    private void addPendingBuffer(final byte[] buffer) {
        if (this.pendingBuffer == null) {
            this.pendingBuffer = buffer;
        } else {
            byte[] newPending = new byte[this.pendingBuffer.length + buffer.length];
            System.arraycopy(this.pendingBuffer, 0, newPending, 0, this.pendingBuffer.length);
            System.arraycopy(buffer, 0, newPending, this.pendingBuffer.length, buffer.length);
            this.pendingBuffer = newPending;
        }
    }

    private void activateNotification(final BluetoothGattCharacteristic characteristic) {
        this.gatt.setCharacteristicNotification(characteristic, true);
        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BleUUID.BleNotificationDescriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        this.gatt.writeCharacteristic(characteristic);
        this.gatt.writeDescriptor(descriptor);
    }

    private BluetoothGattService getCommandInterfaceService() {
        return this.gatt.getService(BleUUID.ActiveLookCommandsInterfaceService);
    }

    private BluetoothGattCharacteristic getFlowControlCharacteristic() {
        return this.getCommandInterfaceService().getCharacteristic(BleUUID.ActiveLookFlowControlCharacteristic);
    }

    private BluetoothGattCharacteristic getTxCharacteristic() {
        return this.getCommandInterfaceService().getCharacteristic(BleUUID.ActiveLookTxCharacteristic);
    }

    private BluetoothGattCharacteristic getUiCharacteristic() {
        return this.getCommandInterfaceService().getCharacteristic(BleUUID.ActiveLookUICharacteristic);
    }

    private BluetoothGattCharacteristic getSensorCharacteristic() {
        return this.getCommandInterfaceService().getCharacteristic(BleUUID.ActiveLookSensorInterfaceCharacteristic);
    }

    private BluetoothGattCharacteristic getRxCharacteristic() {
        return this.getCommandInterfaceService().getCharacteristic(BleUUID.ActiveLookRxCharacteristic);
    }

    public DeviceInformation getDeviceInformation() {
        return this.deviceInfo;
    }

    public void subscribeToBatteryLevelNotifications(Consumer<Integer> onEvent) {
        if (this.onBatteryLevelEvent == null && onEvent != null) {
            this.activateNotification(
                    this.gatt.getService(BleUUID.BatteryService)
                            .getCharacteristic(BleUUID.BatteryLevelCharacteristic));
        }
        this.onBatteryLevelEvent = onEvent;
    }

    public void subscribeToFlowControlNotifications(Consumer<FlowControlStatus> onEvent) {
        this.onFlowControlEvent = onEvent;
    }

    public void subscribeToSensorInterfaceNotifications(Runnable onEvent) {
        if (this.onSensorInterfaceEvent == null && onEvent != null) {
            this.activateNotification(
                    this.gatt.getService(BleUUID.ActiveLookCommandsInterfaceService)
                            .getCharacteristic(BleUUID.ActiveLookSensorInterfaceCharacteristic));
        }
        this.onSensorInterfaceEvent = onEvent;
    }

}
