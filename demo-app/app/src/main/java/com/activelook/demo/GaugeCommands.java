package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;

import java.util.Map;

public class GaugeCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Bitmaps commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
                item("gaugeSave()", glasses ->
                        glasses.gaugeSave(
                                (byte) 0x01,
                                (short) 50, (short) 50,
                                (short) 50, (short) 20,
                                (byte) 30, (byte) 120, true)
                ),
                item("gaugeDisplay()", glasses -> glasses.gaugeDisplay((byte) 0x01, (byte) 0x25)),
        };
    }

}
