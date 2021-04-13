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

import java.util.Arrays;

public class GlassesVersion {

    private int major;
    private int minor;
    private int patch;
    private char extra;
    private int year;
    private int week;
    private byte[] serial;

    public GlassesVersion(byte[] payload) {
        this.major = payload[0];
        this.minor = payload[1];
        this.patch = payload[2];
        this.extra = (char) payload[3];
        this.year = payload[4];
        this.week = payload[5];
        this.serial = new byte[]{
                payload[6],
                payload[7],
                payload[8]
        };
    }

    public String getVersion() {
        return String.format("%d.%d.%d.%c", this.major, this.minor, this.patch, this.extra);
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getPatch() {
        return this.patch;
    }

    public char getExtra() {
        return this.extra;
    }

    public int getYear() {
        return this.year;
    }

    public int getWeek() {
        return this.week;
    }

    public byte[] getSerial() {
        return this.serial;
    }

    @Override
    public String toString() {
        return "Version{" +
                "major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                ", extra=" + extra +
                ", year=" + year +
                ", week=" + week +
                ", serial=" + Arrays.toString(serial) +
                '}';
    }

}
