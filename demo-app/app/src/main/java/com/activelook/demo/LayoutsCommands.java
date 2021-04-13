package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.LayoutParameters;
import com.activelook.activelooksdk.types.Rotation;

import java.util.Map;

public class LayoutsCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Bitmaps commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        LayoutParameters layout = new LayoutParameters(
                (byte) 0x17,
                (short) 12,
                (byte) 27,
                (short) 100,
                (byte) 43,
                (byte) 0x0F,
                (byte) 0x03,
                (byte) 0x01,
                true,
                (short) 0,
                (byte) 0,
                Rotation.TOP_LR,
                false
        )
                .addSubCommandLine((short) 0, (short) 0, (short) 25, (short) 25);
        return new Map.Entry[]{
                item("layoutSave", glasses -> glasses.layoutSave(layout)),
                item("layoutDisplay", glasses -> glasses.layoutDisplay((byte) 0x17, "ABCD")),
                item("layoutDelete", glasses -> glasses.layoutDelete((byte) 0x17)),
        };
    }

}
