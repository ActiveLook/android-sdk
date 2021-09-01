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
package com.activelook.activelooksdk.core;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.GlassesCommands;
import com.activelook.activelooksdk.types.Configuration;
import com.activelook.activelooksdk.types.ConfigurationDescription;
import com.activelook.activelooksdk.types.ConfigurationElementsInfo;
import com.activelook.activelooksdk.types.DemoPattern;
import com.activelook.activelooksdk.types.FontData;
import com.activelook.activelooksdk.types.FontInfo;
import com.activelook.activelooksdk.types.FreeSpace;
import com.activelook.activelooksdk.types.GaugeInfo;
import com.activelook.activelooksdk.types.GlassesSettings;
import com.activelook.activelooksdk.types.GlassesVersion;
import com.activelook.activelooksdk.types.Image1bppData;
import com.activelook.activelooksdk.types.ImageData;
import com.activelook.activelooksdk.types.ImageInfo;
import com.activelook.activelooksdk.types.LayoutParameters;
import com.activelook.activelooksdk.types.LedState;
import com.activelook.activelooksdk.types.PageInfo;
import com.activelook.activelooksdk.types.Rotation;
import com.activelook.activelooksdk.types.SensorMode;
import com.activelook.activelooksdk.types.SensorParameters;
import com.activelook.activelooksdk.types.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class GlassesCommandsAdapter implements GlassesCommands {

    /*
     * General commands ids
     */
    static final byte ID_power = (byte) 0x00;
    static final byte ID_clear = (byte) 0x01;
    static final byte ID_grey = (byte) 0x02;
    static final byte ID_demo = (byte) 0x03;
    static final byte ID_test = (byte) 0x04;
    static final byte ID_battery = (byte) 0x05;
    static final byte ID_vers = (byte) 0x06;
    static final byte ID_led = (byte) 0x08;
    static final byte ID_shift = (byte) 0x09;
    static final byte ID_settings = (byte) 0x0A;
    static final byte ID_setName = (byte) 0x0B;
    /*
     * Display luminance commands ids
     */
    static final byte ID_luma = (byte) 0x10;
    /*
     * Optical sensor commands ids
     */
    static final byte ID_sensor = (byte) 0x20;
    static final byte ID_gesture = (byte) 0x21;
    static final byte ID_als = (byte) 0x22;
    /*
     * Graphics commands ids
     */
    static final byte ID_color = (byte) 0x30;
    static final byte ID_point = (byte) 0x31;
    static final byte ID_line = (byte) 0x32;
    static final byte ID_rect = (byte) 0x33;
    static final byte ID_rectf = (byte) 0x34;
    static final byte ID_circ = (byte) 0x35;
    static final byte ID_circf = (byte) 0x36;
    static final byte ID_txt = (byte) 0x37;
    static final byte ID_polyline = (byte) 0x38;
    /*
     * Images commands ids
     */
    static final byte ID_imgList = (byte) 0x40;
    static final byte ID_imgSave = (byte) 0x41;
    static final byte ID_imgDisplay = (byte) 0x42;
    static final byte ID_imgDelete = (byte) 0x43;
    static final byte ID_imgStream = (byte) 0x44;
    static final byte ID_imgSave1bpp = (byte) 0x45;
    /*
     * Font commands ids
     */
    static final byte ID_fontList = (byte) 0x50;
    static final byte ID_fontSave = (byte) 0x51;
    static final byte ID_fontSelect = (byte) 0x52;
    static final byte ID_fontDelete = (byte) 0x53;
    /*
     * Layout commands ids
     */
    static final byte ID_layoutSave = (byte) 0x60;
    static final byte ID_layoutDelete = (byte) 0x61;
    static final byte ID_layoutDisplay = (byte) 0x62;
    static final byte ID_layoutClear = (byte) 0x63;
    static final byte ID_layoutList = (byte) 0x64;
    static final byte ID_layoutPosition = (byte) 0x65;
    static final byte ID_layoutDisplayExtended = (byte) 0x66;
    static final byte ID_layoutGet = (byte) 0x67;
    /*
     * Gauge commands ids
     */
    static final byte ID_gaugeDisplay = (byte) 0x70;
    static final byte ID_gaugeSave = (byte) 0x71;
    static final byte ID_gaugeDelete = (byte) 0x72;
    static final byte ID_gaugeList = (byte) 0x73;
    static final byte ID_gaugeGet = (byte) 0x74;
    /*
     * Page commands ids
     */
    static final byte ID_pageSave = (byte) 0x80;
    static final byte ID_pageGet = (byte) 0x81;
    static final byte ID_pageDelete = (byte) 0x82;
    static final byte ID_pageDisplay = (byte) 0x83;
    static final byte ID_pageClear = (byte) 0x84;
    static final byte ID_pageList = (byte) 0x85;
    /*
     * Configuration for firmware 1.7 ids
     */
    static final byte ID_tdbg = (byte) 0xA0;
    static final byte ID_WConfigID = (byte) 0xA1;
    static final byte ID_RConfigID = (byte) 0xA2;
    static final byte ID_SetConfigID = (byte) 0xA3;
    /*
     * Statistics commands ids
     */
    static final byte ID_pixelCount = (byte) 0xA5;
    static final byte ID_getChargingCounter = (byte) 0xA7;
    static final byte ID_getChargingTime = (byte) 0xA8;
    static final byte ID_resetChargingParam = (byte) 0xAA;
    /*
     * Configuration commands ids
     */
    static final byte ID_cfgWrite = (byte) 0xD0;
    static final byte ID_cfgRead = (byte) 0xD1;
    static final byte ID_cfgSet = (byte) 0xD2;
    static final byte ID_cfgList = (byte) 0xD3;
    static final byte ID_cfgRename = (byte) 0xD4;
    static final byte ID_cfgDelete = (byte) 0xD5;
    static final byte ID_cfgDeleteLessUsed = (byte) 0xD6;
    static final byte ID_cfgFreeSpace = (byte) 0xD7;
    static final byte ID_cfgGetNb = (byte) 0xD8;
    private final HashMap<QueryId, Consumer<byte[]>> callbacks;
    private QueryId currentQID;

    /*
    Methods for children implementation
     */
    protected GlassesCommandsAdapter() {
        this.currentQID = new QueryId();
        this.callbacks = new HashMap<>();
    }

    protected void writeCommand(byte[] payload) {
    }

    protected final void delegateToCallback(final Payload payload) {
        final QueryId qid = payload.getQueryId();
        if (qid != null) {
            final Consumer<byte[]> callback = this.callbacks.remove(qid);
            if (callback != null) {
                callback.accept(payload.getData());
            }
        }
    }

    /*
    Private helpers
     */
    private void registerCallback(QueryId queryId, Consumer<byte[]> callback) {
        this.callbacks.put(queryId, callback);
    }

    private QueryId nextQueryId() {
        final QueryId result = this.currentQID;
        this.currentQID = this.currentQID.next();
        return result;
    }

    private void write(final Payload payload) {
        final QueryId qid = this.nextQueryId();
        payload.setQueryId(qid);
        this.writeCommand(payload.toBytes());
    }

    private void write(final Payload payload, final Consumer<byte[]> callback) {
        QueryId qid = this.nextQueryId();
        payload.setQueryId(qid);
        this.registerCallback(qid, callback);
        this.writeCommand(payload.toBytes());
    }

    /*
    Public defaults
     */
    @Override
    public void loadConfiguration(BufferedReader cfg) throws IOException {
        String line = "";
        while ((line = cfg.readLine()) != null) {
            this.writeCommand(Utils.hexStringToBytes(line));
        }
    }

    @Override
    public void power(boolean on) {
        this.write(new Payload(ID_power).addData(on));
    }

    @Override
    public void clear() {
        this.write(new Payload(ID_clear));
    }

    @Override
    public void grey(byte level) {
        this.write(new Payload(ID_grey).addData(level));
    }

    @Override
    public void demo() {
        this.write(new Payload(ID_demo));
    }

    @Override
    public void demo(DemoPattern pattern) {
        this.write(new Payload(ID_demo).addData(pattern.toBytes()));
    }

    @Override
    public void test(DemoPattern pattern) {
        this.write(new Payload(ID_test).addData(pattern.toBytes()));
    }

    @Override
    public void battery(Consumer<Integer> onResult) {
        this.write(new Payload(ID_battery), bytes -> onResult.accept((int) bytes[0]));
    }

    @Override
    public void vers(Consumer<GlassesVersion> onResult) {
        this.write(new Payload(ID_vers), bytes -> onResult.accept(new GlassesVersion(bytes)));
    }

    @Override
    public void led(LedState state) {
        this.write(new Payload(ID_led).addData(state.toBytes()));
    }

    @Override
    public void shift(short x, short y) {
        this.write(new Payload(ID_shift).addData(x).addData(y));
    }

    @Override
    public void settings(Consumer<GlassesSettings> onResult) {
        this.write(new Payload(ID_settings), bytes -> onResult.accept(new GlassesSettings(bytes)));
    }

    @Override
    public void setName(String name) {
        this.write(new Payload(ID_setName).addData(name));
    }

    @Override
    public void luma(byte value) {
        this.write(new Payload(ID_luma).addData(value));
    }

    @Override
    public void sensor(boolean on) {
        this.write(new Payload(ID_sensor).addData(on));
    }

    @Override
    public void gesture(boolean on) {
        this.write(new Payload(ID_gesture).addData(on));
    }

    @Override
    public void als(boolean on) {
        this.write(new Payload(ID_als).addData(on));
    }

    @Override
    public void color(byte value) {
        this.write(new Payload(ID_color).addData(value));
    }

    @Override
    public void point(short x, short y) {
        this.write(new Payload(ID_point).addData(x).addData(y));
    }

    @Override
    public void line(short x1, short y1, short x2, short y2) {
        this.write(new Payload(ID_line).addData(x1).addData(y1).addData(x2).addData(y2));
    }

    @Override
    public void rect(short x1, short y1, short x2, short y2) {
        this.write(new Payload(ID_rect).addData(x1).addData(y1).addData(x2).addData(y2));
    }

    @Override
    public void rectf(short x1, short y1, short x2, short y2) {
        this.write(new Payload(ID_rectf).addData(x1).addData(y1).addData(x2).addData(y2));
    }

    @Override
    public void circ(short x, short y, byte r) {
        this.write(new Payload(ID_circ).addData(x).addData(y).addData(r));
    }

    @Override
    public void circf(short x, short y, byte r) {
        this.write(new Payload(ID_circf).addData(x).addData(y).addData(r));
    }

    @Override
    public void txt(short x, short y, Rotation r, byte f, byte c, String s) {
        this.write(new Payload(ID_txt).addData(x).addData(y).addData(r.toBytes()).addData(f).addData(c).addData(s,
                true));
    }

    @Override
    public void polyline(short[] points) {
        this.write(new Payload(ID_polyline).addData(points));
    }

    @Override
    public void imgList(Consumer<List<ImageInfo>> onResult) {
        this.write(
                new Payload(ID_imgList),
                bytes -> onResult.accept(ImageInfo.toList(bytes))
        );
    }

    @Override
    public void imgSave(byte id, ImageData data) {
        this.write(new Payload(ID_imgSave)
                .addData(id)
                .addData(data.getSize())
                .addData(data.getWidth())
        );
        for (byte[] chunk : data.getChunks(240)) {
            this.write(new Payload(ID_imgSave).addData(chunk));
        }
    }

    @Override
    public void imgDisplay(byte id, short x, short y) {
        this.write(new Payload(ID_imgDisplay).addData(id).addData(x).addData(y));
    }

    @Override
    public void imgDelete(byte id) {
        this.write(new Payload(ID_imgDelete).addData(id));
    }

    @Override
    public void imgDeleteAll() {
        this.imgDelete((byte) 0xFF);
    }

    @Override
    public void imgStream(Image1bppData data, short x, short y) {
        this.write(new Payload(ID_imgStream)
                .addData(data.getSize())
                .addData(data.getWidth())
                .addData(x)
                .addData(y)
        );
        for (byte[] chunk : data.getChunks(240)) {
            this.write(new Payload(ID_imgStream).addData(chunk));
        }
    }

    @Override
    public void imgSave1bpp(Image1bppData data) {
        this.write(new Payload(ID_imgSave1bpp)
                .addData(data.getSize())
                .addData(data.getWidth())
        );
        for (byte[] chunk : data.getChunks(240)) {
            this.write(new Payload(ID_imgSave1bpp).addData(chunk));
        }
    }

    @Override
    public void fontList(Consumer<List<FontInfo>> onResult) {
        this.write(new Payload(ID_fontList), bytes -> onResult.accept(FontInfo.toList(bytes)));
    }

    @Override
    public void fontSave(byte id, FontData data) {
        this.write(new Payload(ID_fontSave)
                .addData(id)
                .addData(data.getFontSize())
        );
        for (byte[] chunk : data.getChunks(240)) {
            this.write(new Payload(ID_fontSave).addData(chunk));
        }
    }

    @Override
    public void fontSelect(byte id) {
        this.write(new Payload(ID_fontSelect).addData(id));
    }

    @Override
    public void fontDelete(byte id) {
        this.write(new Payload(ID_fontDelete).addData(id));
    }

    @Override
    public void fontDeleteAll() {
        this.fontDelete((byte) 0xFF);
    }

    @Override
    public void layoutSave(LayoutParameters layout) {
        this.write(new Payload(ID_layoutSave).addData(layout.toBytes()));
    }

    @Override
    public void layoutDelete(byte id) {
        this.write(new Payload(ID_layoutDelete).addData(id));
    }

    @Override
    public void layoutDeleteAll() {
        this.layoutDelete((byte) 0xFF);
    }

    @Override
    public void layoutDisplay(byte id, String text) {
        this.write(new Payload(ID_layoutDisplay).addData(id).addData(text, true));
    }

    @Override
    public void layoutClear(byte id) {
        this.write(new Payload(ID_layoutClear).addData(id));
    }

    @Override
    public void layoutList(Consumer<List<Integer>> onResult) {
        this.write(
            new Payload(ID_layoutList),
            bytes -> {
                final List<Integer> r = new ArrayList<>();
                for (byte b: bytes) {
                    r.add(b & 0x00FF);
                }
                onResult.accept(r);
            }
        );
    }

    @Override
    public void layoutPosition(byte id, short x, byte y) {
        this.write(new Payload(ID_layoutPosition).addData(id).addData(x).addData(y));
    }

    @Override
    public void layoutDisplayExtended(byte id, short x, byte y, String text) {
        this.write(new Payload(ID_layoutDisplayExtended).addData(id).addData(x).addData(y).addData(text, true));
    }

    @Override
    public void layoutGet(byte id, Consumer<LayoutParameters> onResult) {
        this.write(
                new Payload(ID_layoutList).addData(id),
                bytes -> onResult.accept(new LayoutParameters(bytes))
        );
    }

    @Override
    public void gaugeDisplay(byte id, byte value) {
        this.write(new Payload(ID_gaugeDisplay).addData(id).addData(value));
    }

    @Override
    public void gaugeSave(byte id, short x, short y, char r, char rin, byte start, byte end, boolean clockwise) {
        this.write(new Payload(ID_gaugeSave)
                .addData(id)
                .addData(x).addData(y)
                .addData(r).addData(rin)
                .addData(start).addData(end)
                .addData(clockwise));
    }

    @Override
    public void gaugeSave(byte id, GaugeInfo gaugeInfo) {
        this.gaugeSave(
            id,
            gaugeInfo.getX(),
            gaugeInfo.getY(),
            gaugeInfo.getR(),
            gaugeInfo.getRin(),
            gaugeInfo.getStart(),
            gaugeInfo.getEnd(),
            gaugeInfo.isClockwise()
        );
    }

    @Override
    public void gaugeDelete(byte id) {
        this.write(new Payload(ID_gaugeDelete).addData(id));
    }

    @Override
    public void gaugeDeleteAll() {
        this.gaugeDelete((byte) 0xFF);
    }

    @Override
    public void gaugeList(Consumer<List<Integer>> onResult) {
        this.write(
                new Payload(ID_gaugeList),
                bytes -> {
                    final List<Integer> r = new ArrayList<>();
                    for (byte b: bytes) {
                        r.add(b & 0x00FF);
                    }
                    onResult.accept(r);
                }
        );
    }

    @Override
    public void gaugeGet(byte id, Consumer<GaugeInfo> onResult) {
        this.write(
                new Payload(ID_gaugeGet).addData(id),
                bytes -> onResult.accept(new GaugeInfo(bytes))
        );
    }

    @Override
    public void pageSave(byte id, byte[] layoutIds, short[] xs, byte [] ys) {
        this.pageSave(new PageInfo(id, layoutIds, xs, ys));
    }

    @Override
    public void pageSave(PageInfo page) {
        this.write(new Payload(ID_pageSave).addData(page.getPayload()));
    }

    @Override
    public void pageGet(byte id, Consumer<PageInfo> onResult) {
        this.write(new Payload(ID_pageList), bytes -> onResult.accept(new PageInfo(bytes)));
    }

    @Override
    public void pageDelete(byte id) {
        this.write(new Payload(ID_pageDelete).addData(id));
    }

    @Override
    public void pageDeleteAll() {
        this.pageDelete((byte) 0xFF);
    }

    @Override
    public void pageDisplay(byte id, String [] texts) {
        final Payload payload = new Payload(ID_pageDisplay).addData(id);
        for(String text: texts) {
            payload.addData(text, true);
        }
        this.write(payload);
    }

    @Override
    public void pageClear(byte id) {
        this.write(new Payload(ID_pageClear).addData(id));
    }
    @Override
    public void pageList(Consumer<List<Integer>> onResult) {
        this.write(
                new Payload(ID_pageList),
                bytes -> {
                    final List<Integer> r = new ArrayList<>();
                    for (byte b: bytes) {
                        r.add(b & 0x00FF);
                    }
                    onResult.accept(r);
                }
        );
    }

    @Override
    public void pixelCount(Consumer<Integer> onResult) {
        this.write(new Payload(ID_pixelCount), bytes -> onResult.accept((int) bytes[0]));
    }

    @Override
    public void getChargingCounter(Consumer<Integer> onResult) {
        this.write(new Payload(ID_getChargingCounter), bytes -> onResult.accept((int) bytes[0]));
    }

    @Override
    public void getChargingTime(Consumer<Integer> onResult) {
        this.write(new Payload(ID_getChargingTime), bytes -> onResult.accept((int) bytes[0]));
    }

    @Override
    public void resetChargingParam() {
        this.write(new Payload(ID_resetChargingParam));
    }

    @Override
    public void cfgWrite(String name, int version, int password) {
        this.write(new Payload(ID_cfgWrite)
                .addData(name, true)
                .addData(version)
                .addData(password)
        );
    }
    @Override
    public void cfgRead(String name, Consumer<ConfigurationElementsInfo> onResult) {
        this.write(
                new Payload(ID_cfgRead).addData(name, true),
                bytes -> onResult.accept(new ConfigurationElementsInfo(bytes))
        );
    }
    @Override
    public void cfgSet(String name) {
        this.write(new Payload(ID_cfgSet).addData(name, true));
    }
    @Override
    public void cfgList(Consumer<List<ConfigurationDescription>> onResult) {
        this.write(
                new Payload(ID_cfgList),
                bytes -> onResult.accept(ConfigurationDescription.toList(bytes))
        );
    }
    @Override
    public void cfgRename(String oldName, String newName, int password) {
        this.write(new Payload(ID_cfgRename)
                .addData(oldName, true)
                .addData(newName, true)
                .addData(password)
        );
    }
    @Override
    public void cfgDelete(String name) {
        this.write(new Payload(ID_cfgDelete).addData(name, true));
    }
    @Override
    public void cfgDeleteLessUsed() {
        this.write(new Payload(ID_cfgDeleteLessUsed));
    }
    @Override
    public void cfgFreeSpace(Consumer<FreeSpace> onResult) {
        this.write(
                new Payload(ID_cfgFreeSpace),
                bytes -> onResult.accept(new FreeSpace(bytes))
        );
    }
    @Override
    public void cfgGetNb(Consumer<Integer> onResult) {
        this.write(
                new Payload(ID_cfgGetNb),
                bytes -> onResult.accept((int) bytes[0])
        );
    }

    ///////////////////////
    /* Firmware 1.7 only */
    ///////////////////////
    @Override
    public void tdbg() {
        this.write(new Payload(ID_tdbg));
    }

    @Override
    public void WConfigID(Configuration config) {
        this.write(new Payload(ID_WConfigID).addData(config.toBytes()));
    }

    @Override
    public void RConfigID(byte number, Consumer<Configuration> onResult) {
        this.write(new Payload(ID_RConfigID).addData(number), bytes -> onResult.accept(new Configuration(bytes)));
    }

    @Override
    public void SetConfigID(byte id) {
        this.write(new Payload(ID_SetConfigID).addData(id));
    }

}
