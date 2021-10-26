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
package com.activelook.activelooksdk.types;

public class GlassesSettings {

    private int globalXShift;
    private int globalYShift;
    private int luma;
    private boolean alsEnable;
    private boolean gestureEnable;

    public GlassesSettings(byte[] payload) {
        this.globalXShift = payload[0];
        this.globalYShift = payload[1];
        this.luma = payload[2];
        this.alsEnable = payload[3] == 0x01;
        this.gestureEnable = payload[4] == 0x01;
    }

    public int getGlobalXShift() {
        return this.globalXShift;
    }

    public int getGlobalYShift() {
        return this.globalYShift;
    }

    public int getLuma() {
        return this.luma;
    }

    public boolean isAlsEnable() {
        return this.alsEnable;
    }

    public boolean isGestureEnable() {
        return this.gestureEnable;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "globalXShift=" + globalXShift +
                ", globalYShift=" + globalYShift +
                ", luma=" + luma +
                ", alsEnable=" + alsEnable +
                ", gestureEnable=" + gestureEnable +
                '}';
    }

}
