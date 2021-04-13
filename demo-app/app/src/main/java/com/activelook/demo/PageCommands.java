package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;

import java.util.Map;

public class PageCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Bitmaps commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
        };
    }

}
