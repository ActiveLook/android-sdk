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

public final class GlassesVersion {

    private short major;
    private short minor;
    private short patch;
    private char extra;
    private short year;
    private short week;
    private int serial;

    public GlassesVersion(final short major, final short minor, final short patch, final char extra, final short year,
                          final short week, final int serial) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.extra = extra;
        this.year = year;
        this.week = week;
        this.serial = serial;
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

    public int getSerial() {
        return this.serial;
    }

    @Override
    public String toString() {
        return "GlassesVersion " +
                "{ major=" + this.major +
                ", minor=" + this.minor +
                ", patch=" + this.patch +
                ", extra=" + this.extra +
                ", year=" + this.year +
                ", week=" + this.week +
                ", serial=" + this.serial +
                ", version=" + this.getVersion() +
                " }";
    }

}
