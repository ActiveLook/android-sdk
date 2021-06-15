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

import com.activelook.activelooksdk.core.Payload;

import java.util.ArrayList;
import java.util.List;

public class LayoutParameters {

    private final byte id;
    private final short x;
    private final byte y;
    private final short width;
    private final byte height;
    private final byte fg;
    private final byte bg;
    private final byte font;
    private final boolean textValid;
    private final short textX;
    private final byte textY;
    private final Rotation rotation;
    private final boolean textOpacity;
    private Payload subCommands;

    public LayoutParameters(byte id,
                            short x, byte y, short width, byte height,
                            byte fg, byte bg, byte font, boolean textValid,
                            short textX, byte textY, Rotation rotation, boolean textOpacity) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fg = fg;
        this.bg = bg;
        this.font = font;
        this.textValid = textValid;
        this.textX = textX;
        this.textY = textY;
        this.rotation = rotation;
        this.textOpacity = textOpacity;
        this.subCommands = new Payload();
    }

    public LayoutParameters(byte[] bytes) {
        this(
            (byte) 0,
            (short) ((bytes[1] << 8) | bytes[2]),
            bytes[3],
            (short) ((bytes[4] << 8) | bytes[5]),
            bytes[6],
            bytes[7],
            bytes[8],
            bytes[9],
            bytes[10] != 0x00,
            (short) ((bytes[11] << 8) | bytes[12]),
            bytes[13],
            Utils.toRotation(bytes[14]),
            bytes[15] != 0x00
        );
        final int subSize = bytes[0];
        final byte [] subPayload = new byte [subSize];
        System.arraycopy(bytes, 15, subPayload, 0, subSize);
        this.subCommands.addData(subPayload);
    }

    public LayoutParameters addSubCommandBitmap(byte id, short x, short y) {
        this.subCommands.addData((byte) 0x00).addData(id).addData(x).addData(y);
        return this;
    }

    public LayoutParameters addSubCommandCirc(short x, short y, short r) {
        this.subCommands.addData((byte) 0x01).addData(x).addData(y).addData(r);
        return this;
    }

    public LayoutParameters addSubCommandCircf(short x, short y, short r) {
        this.subCommands.addData((byte) 0x02).addData(x).addData(y).addData(r);
        return this;
    }

    public LayoutParameters addSubCommandColor(byte c) {
        this.subCommands.addData((byte) 0x03).addData(c);
        return this;
    }

    public LayoutParameters addSubCommandFont(byte f) {
        this.subCommands.addData((byte) 0x04).addData(f);
        return this;
    }

    public LayoutParameters addSubCommandLine(short x1, short y1, short x2, short y2) {
        this.subCommands.addData((byte) 0x05).addData(x1).addData(y1).addData(x2).addData(y2);
        return this;
    }

    public LayoutParameters addSubCommandPoint(byte x, short y) {
        this.subCommands.addData((byte) 0x06).addData(x).addData(y);
        return this;
    }

    public LayoutParameters addSubCommandRect(short x1, short y1, short x2, short y2) {
        this.subCommands.addData((byte) 0x07).addData(x1).addData(y1).addData(x2).addData(y2);
        return this;
    }

    public LayoutParameters addSubCommandRectf(short x1, short y1, short x2, short y2) {
        this.subCommands.addData((byte) 0x08).addData(x1).addData(y1).addData(x2).addData(y2);
        return this;
    }

    public LayoutParameters addSubCommandText(short x, short y, String txt) {
        this.subCommands.addData((byte) 0x09).addData(x).addData(y).addData((byte) txt.length()).addData(txt);
        return this;
    }

    public LayoutParameters addSubCommandGauge(byte gaugeId) {
        this.subCommands.addData((byte) 0x0A).addData(gaugeId);
        return this;
    }

    public byte[] toBytes() {
        final byte[] subBytes = this.subCommands.getData();
        return new Payload()
                .addData(this.id)
                .addData((byte) subBytes.length)
                .addData(this.x)
                .addData(this.y)
                .addData(this.width)
                .addData(this.height)
                .addData(this.fg)
                .addData(this.bg)
                .addData(this.font)
                .addData(this.textValid)
                .addData(this.textX)
                .addData(this.textY)
                .addData(this.rotation.toBytes())
                .addData(this.textOpacity)
                .addData(subBytes).getData();
    }

}
