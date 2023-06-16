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

import android.graphics.Point;

import com.activelook.activelooksdk.core.CommandData;

import java.util.List;

public class LayoutExtraCmd {
    private final CommandData subCommands;

    public LayoutExtraCmd() {
        this.subCommands = new CommandData();
    }

    public LayoutExtraCmd addSubCommandBitmap(byte id, short x, short y) {
        this.subCommands.add((byte) 0x00).addUInt8(id).addInt16(x).addInt16(y);
        return this;
    }

    public LayoutExtraCmd addSubCommandCirc(short x, short y, short r) {
        this.subCommands.add((byte) 0x01).addInt16(x).addInt16(y).addUInt16(r);
        return this;
    }

    public LayoutExtraCmd addSubCommandCircf(short x, short y, short r) {
        this.subCommands.add((byte) 0x02).addInt16(x).addInt16(y).addUInt16(r);
        return this;
    }

    public LayoutExtraCmd addSubCommandColor(byte c) {
        this.subCommands.add((byte) 0x03).addUInt8(c);
        return this;
    }

    public LayoutExtraCmd addSubCommandFont(byte f) {
        this.subCommands.add((byte) 0x04).addUInt8(f);
        return this;
    }

    public LayoutExtraCmd addSubCommandLine(short x1, short y1, short x2, short y2) {
        this.subCommands.add((byte) 0x05).addInt16(x1).addInt16(y1).addInt16(x2).addInt16(y2);
        return this;
    }

    public LayoutExtraCmd addSubCommandPoint(short x, short y) {
        this.subCommands.add((byte) 0x06).addInt16(x).addInt16(y);
        return this;
    }

    public LayoutExtraCmd addSubCommandRect(short x1, short y1, short x2, short y2) {
        this.subCommands.add((byte) 0x07).addInt16(x1).addInt16(y1).addInt16(x2).addInt16(y2);
        return this;
    }

    public LayoutExtraCmd addSubCommandRectf(short x1, short y1, short x2, short y2) {
        this.subCommands.add((byte) 0x08).addInt16(x1).addInt16(y1).addInt16(x2).addInt16(y2);
        return this;
    }

    public LayoutExtraCmd addSubCommandText(short x, short y, String txt) {
        this.subCommands.add((byte) 0x09).addInt16(x).addInt16(y).addUInt8((short) (txt.length()+1)).addNulTerminatedStrings(txt);
        return this;
    }

    public LayoutExtraCmd addSubCommandGauge(byte gaugeId) {
        this.subCommands.add((byte) 0x0A).addUInt8(gaugeId);
        return this;
    }

    public LayoutExtraCmd addSubCommandAnim(byte handlerId, byte id, short delay, byte repeat, short x, short y) {
        this.subCommands.add((byte) 0x0B).addUInt8(handlerId).addUInt8(id).addInt16(delay).addUInt8(repeat).addInt16(x).addInt16(y);
        return this;
    }

    public LayoutExtraCmd addSubCommandPolyline(byte thickness, List<Point> points) {
        final byte reserved = 0;

        this.subCommands.add((byte) 0x0C).addUInt8((short) points.size()).addUInt8(thickness).addUInt8(reserved).addUInt8(reserved);

        for (Point p : points) {
            this.subCommands.addInt16((short) p.x).addInt16((short) p.y);
        }
        return this;
    }

    public byte[] toBytes() {
        final byte[] subBytes = this.subCommands.getBytes();
        return new CommandData()
                .add(subBytes).getBytes();
    }
}
