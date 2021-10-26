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

import java.util.ArrayList;
import java.util.List;

public class GaugeInfo {

    private final short x;
    private final short y;
    private final char r;
    private final char rin;
    private final byte start;
    private final byte end;
    private final boolean clockwise;

    public GaugeInfo(byte [] payload) {
        this(
            (short) (((payload[0] & 0xFF) << 8) | (payload[1] & 0xFF)),
            (short) (((payload[2] & 0xFF) << 8) | (payload[3] & 0xFF)),
            (char) (((payload[4] & 0xFF) << 8) | (payload[5] & 0xFF)),
            (char) (((payload[6] & 0xFF) << 8) | (payload[7] & 0xFF)),
            payload[8],
            payload[9],
            payload[10] != 0x00
        );
    }

    public GaugeInfo(short x, short y, char r, char rin, byte start, byte end, boolean clockwise) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.rin = rin;
        this.start = start;
        this.end = end;
        this.clockwise = clockwise;
    }

    public short getX() {
        return this.x;
    }

    public short getY() {
        return this.y;
    }

    public char getR() {
        return this.r;
    }

    public char getRin() {
        return this.rin;
    }

    public byte getStart() {
        return this.start;
    }

    public byte getEnd() {
        return this.end;
    }

    public boolean isClockwise() {
        return this.clockwise;
    }

    @Override
    public String toString() {
        return "GaugeInfo{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + (short) r +
                ", rin=" + (short) rin +
                ", start=" + start +
                ", end=" + end +
                ", clockwise=" + clockwise +
                '}';
    }

}
