package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;

import java.util.Map;

public class DisplayLuminanceCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Display luminance commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
                item("luma(3)", glasses -> glasses.luma((byte) 3)),
                item("luma(7)", glasses -> glasses.luma((byte) 7)),
                item("luma(11)", glasses -> glasses.luma((byte) 11)),
                item("luma(15)", glasses -> glasses.luma((byte) 15)),
        };
    }

}
