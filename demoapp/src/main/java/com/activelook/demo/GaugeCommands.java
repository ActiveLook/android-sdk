package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.GaugeInfo;

import java.util.Arrays;
import java.util.Map;

public class GaugeCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Gauge commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
                item("clear", glasses -> {
                    glasses.clear();
                }),
                item("gaugeDisplay", glasses -> {
                    glasses.gaugeDisplay((byte) 0x01, (byte) 0x50);
                }),
                item("gaugeSave", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.cfgSet("DemoApp");
                    glasses.gaugeSave(
                        (byte) 0x01,
                        (short) 100, (short) 100,
                        (char) 180, (char) 30,
                        (byte) 0x03, (byte) 0x09,
                        true
                    );
                }),
                item("gaugeDelete", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.cfgSet("DemoApp");
                    glasses.gaugeDelete((byte) 0x01);
                }),
                item("gaugeList", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.cfgSet("DemoApp");
                    glasses.gaugeList(
                        r -> GaugeCommands.this.snack(String.format("gaugeList: %s", Arrays.toString(r.toArray())))
                    );
                }),
                item("gaugeGet", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.cfgSet("DemoApp");
                    glasses.gaugeGet(
                        (byte) 0x01,
                        r -> GaugeCommands.this.snack(String.format("gaugeGet: %s", r))
                    );
                }),
        };
    }

}
