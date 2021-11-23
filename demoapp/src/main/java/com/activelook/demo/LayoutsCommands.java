package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.LayoutParameters;
import com.activelook.activelooksdk.types.Rotation;

import java.util.Arrays;
import java.util.Map;

public class LayoutsCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Layouts commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        LayoutParameters layout1 = new LayoutParameters((byte) 0x00,
                (short) 0, (byte) 0, (short) 200, (byte) 50, (byte) 0x0F, (byte) 0x06,
                (byte) 0x00, true, (short) 100, (byte) 25, Rotation.TOP_LR,true)
                .addSubCommandLine((short) 0, (short) 0, (short) 200, (short) 50);
        LayoutParameters layout2 = new LayoutParameters((byte) 0x01,
                (short) 0, (byte) 100, (short) 200, (byte) 50, (byte) 0x0F, (byte) 0x09,
                (byte) 0x00, true, (short) 100, (byte) 25, Rotation.TOP_LR,true)
                .addSubCommandLine((short) 0, (short) 0, (short) 200, (short) 50);
        LayoutParameters layout3 = new LayoutParameters((byte) 0x02,
                (short) 0, (byte) 200, (short) 200, (byte) 50, (byte) 0x0F, (byte) 0x0C,
                (byte) 0x00, true, (short) 100, (byte) 25, Rotation.TOP_LR,true)
                .addSubCommandLine((short) 0, (short) 0, (short) 200, (short) 50);
        return new Map.Entry[]{
                item("clear", glasses -> {
                    glasses.clear();
                }),
                item("layoutSave", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.layoutSave(layout1);
                    glasses.layoutSave(layout2);
                    glasses.layoutSave(layout3);
                }),
                item("layoutDelete", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.layoutDelete((byte) 0x02);
                }),
                item("layoutDeleteAll", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.layoutDeleteAll();
                }),
                item("layoutDisplay", glasses -> {
                    glasses.cfgSet("DemoApp");
                    glasses.layoutDisplay((byte) 0x00, "On L1");
                    glasses.layoutDisplay((byte) 0x01, "On L2");
                    glasses.layoutDisplay((byte) 0x02, "On L3");
                }),
                item("layoutClear", glasses -> {
                    glasses.cfgSet("DemoApp");
                    glasses.layoutClear((byte) 0x00);
                    glasses.layoutClear((byte) 0x01);
                    glasses.layoutClear((byte) 0x02);
                }),
                item("layoutList", glasses -> {
                    glasses.cfgSet("DemoApp");
                    glasses.layoutList(
                        r -> LayoutsCommands.this.snack(String.format("layoutList: %s", Arrays.toString(r.toArray())))
                    );
                }),
                item("layoutPosition", glasses -> {
                    glasses.cfgSet("DemoApp");
                    glasses.layoutPosition((byte) 0x01, (short) 100, (byte) 80);
                    glasses.layoutDisplay((byte) 0x01, "On L2.2");
                }),
                item("layoutDisplayExtended", glasses -> {
                    glasses.cfgSet("DemoApp");
                    glasses.layoutDisplayExtended((byte) 0x00, (short) 100, (byte) 80, "On L1.e2");
                }),
                item("layoutGet", glasses -> {
                    glasses.cfgSet("DemoApp");
                    glasses.layoutGet(
                            (byte) 0x00,
                            r -> LayoutsCommands.this.snack(String.format("layoutGet: %s", r))
                    );
                }),

        };
    }

}
