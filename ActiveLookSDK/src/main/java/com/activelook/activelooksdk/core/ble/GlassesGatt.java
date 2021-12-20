package com.activelook.activelooksdk.core.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.List;
import java.util.UUID;

final class GlassesGatt extends BluetoothGattCallback {

    private final BluetoothGatt gattDelegate;

    GlassesGatt(
            final Context context,
            final BluetoothDevice device,
            final boolean autoConnect) {
        super();
        this.gattDelegate = device.connectGatt(context, autoConnect, this);
    }

    void close() {
        gattDelegate.close();
    }

    void disconnect() {
        gattDelegate.disconnect();
    }

    boolean connect() {
        return gattDelegate.connect();
    }

    BluetoothDevice getDevice() {
        return gattDelegate.getDevice();
    }

    boolean discoverServices() {
        return gattDelegate.discoverServices();
    }

    List<BluetoothGattService> getServices() {
        return gattDelegate.getServices();
    }

    BluetoothGattService getService(UUID uuid) {
        return gattDelegate.getService(uuid);
    }

    boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        return gattDelegate.readCharacteristic(characteristic);
    }

    boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        return gattDelegate.writeCharacteristic(characteristic);
    }

    boolean readDescriptor(BluetoothGattDescriptor descriptor) {
        return gattDelegate.readDescriptor(descriptor);
    }

    boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        return gattDelegate.writeDescriptor(descriptor);
    }

    boolean beginReliableWrite() {
        return gattDelegate.beginReliableWrite();
    }

    boolean executeReliableWrite() {
        return gattDelegate.executeReliableWrite();
    }

    void abortReliableWrite() {
        gattDelegate.abortReliableWrite();
    }

    boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        return gattDelegate.setCharacteristicNotification(characteristic, enable);
    }

    boolean readRemoteRssi() {
        return gattDelegate.readRemoteRssi();
    }

    boolean requestMtu(int mtu) {
        return gattDelegate.requestMtu(mtu);
    }

    boolean requestConnectionPriority(int connectionPriority) {
        return gattDelegate.requestConnectionPriority(connectionPriority);
    }

    int getConnectionState(BluetoothDevice device) {
        return gattDelegate.getConnectionState(device);
    }

    List<BluetoothDevice> getConnectedDevices() {
        return gattDelegate.getConnectedDevices();
    }

    List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        return gattDelegate.getDevicesMatchingConnectionStates(states);
    }

    // BluetoothGattCallback
    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyUpdate(gatt, txPhy, rxPhy, status);
    }

    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyRead(gatt, txPhy, rxPhy, status);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
    }

    @Override
    public void onServiceChanged(BluetoothGatt gatt) {
        super.onServiceChanged(gatt);
    }

}
