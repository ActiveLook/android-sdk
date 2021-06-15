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

import android.content.Context;

import androidx.core.util.Consumer;

/**
 * Main entry point for the Active Look SDK.
 * Every application must first get an instance of the SDK from here.
 */
public interface Sdk {

    /**
     * Initialize the Active Look SDK with the associated context.
     *
     * @param applicationContext The Application context
     * @return The Active Look SDK instance
     */
    static Sdk init(Context applicationContext) {
        return SdkSingleton.init(applicationContext);
    }
    /**
     * Get the Active Look SDK instance.
     *
     * @return The Active Look SDK instance
     */
    static Sdk getInstance() {
        return SdkSingleton.getInstance();
    }
    /**
     * Enter scan mode for searching glasses.
     *
     * @param onDiscoverGlasses The callback to call for each discovered glasses
     */
    void startScan(Consumer<DiscoveredGlasses> onDiscoverGlasses);
    /**
     * Quit scan mode.
     */
    void stopScan();
    /**
     * Is it scanning.
     */
    boolean isScanning();

}
