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

import android.content.Context;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.types.GlassesUpdate;

public final class BleSdkSingleton {

    private static SdkImpl singleton;

    public static final synchronized SdkImpl init(Context applicationContext,
                                                  String token,
                                                  Consumer<GlassesUpdate> onUpdateStart,
                                                  Consumer<GlassesUpdate> onUpdateProgress,
                                                  Consumer<GlassesUpdate> onUpdateSuccess,
                                                  Consumer<GlassesUpdate> onUpdateError) {
        if (BleSdkSingleton.singleton == null) {
            BleSdkSingleton.singleton = new SdkImpl(applicationContext, token, onUpdateStart,
                    onUpdateProgress, onUpdateSuccess, onUpdateError);
        }
        return BleSdkSingleton.singleton;
    }

    public static final synchronized SdkImpl getInstance() {
        return BleSdkSingleton.singleton;
    }

}
