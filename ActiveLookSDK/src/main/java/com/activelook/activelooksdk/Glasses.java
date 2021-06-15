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
package com.activelook.activelooksdk;

import android.os.Parcelable;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.types.DeviceInformation;
import com.activelook.activelooksdk.types.FlowControlStatus;

@SuppressWarnings("SpellCheckingInspection")
public interface Glasses extends GlassesCommands, Parcelable {

    /**
     * Get the manufacturer of the glasses.
     *
     * @return The manufacturer name
     */
    String getManufacturer();
    /**
     * Get the name of the glasses.
     *
     * @return The name
     */
    String getName();
    /**
     * Get the address of the glasses.
     *
     * @return The address
     */
    String getAddress();
    /**
     * Check if the glasses firmware is at least of the one provvided.
     *
     * @param version the version to compare the firmware version to.
     * @return true if the argument version is lower or equal to the glasses firmware version and false otherwise.
     */
    boolean isFirmwareAtLeast(String version);
    /**
     * Compare the glasses firmware version with the version
     *
     * @param version the version to compare the firmware version to.
     * @return the value 0 if the argument version is equal to the glasses firmware version; a value less than 0 if
     * the glasses firmware version is lower than the version argument; and a value greater than 0 if the glasses
     * firmware version is greater than the version argument.
     */
    int compareFirmwareVersion(String version);
    /**
     * Disconnect.
     */
    void disconnect();
    /**
     * Set callback for disconnected event.
     *
     * @param onDisconnected the callback to call when disconnected
     */
    void setOnDisconnected(Consumer<Glasses> onDisconnected);
    /**
     * Get the device public information.
     *
     * @return The device information
     */
    DeviceInformation getDeviceInformation();
    /**
     * Set the callback to call on battery level notifications.
     *
     * @param onEvent The callback to call on BatteryLevel notifications
     */
    void subscribeToBatteryLevelNotifications(Consumer<Integer> onEvent);
    /**
     * Set the callback to call on flow control notifications.
     *
     * @param onEvent The callback to call on FlowControl notifications
     */
    void subscribeToFlowControlNotifications(Consumer<FlowControlStatus> onEvent);
    /**
     * Set the callback to call on sensor interface notifications.
     *
     * @param onEvent The callback to call on SensorInterface notifications
     */
    void subscribeToSensorInterfaceNotifications(Runnable onEvent);
    /**
     * Unset the callback to call on battery level notifications.
     */
    default void unsubscribeToBatteryLevelNotifications() {
        this.subscribeToBatteryLevelNotifications(null);
    }
    /**
     * Unset the callback to call on flow control notifications.
     */
    default void unsubscribeToFlowControlNotifications() {
        this.subscribeToFlowControlNotifications(null);
    }
    /**
     * Unset the callback to call on sensor interface notifications.
     */
    default void unsubscribeToSensorInterfaceNotifications() {
        this.subscribeToSensorInterfaceNotifications(null);
    }

}
