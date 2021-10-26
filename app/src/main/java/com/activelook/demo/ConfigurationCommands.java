package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.Configuration;

import java.util.Arrays;
import java.util.Map;

public class ConfigurationCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Configuration commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
                item("cfgWrite", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                }),
                item("cfgRead", glasses -> {
                    glasses.cfgRead(
                        "DemoApp",
                        r -> snack(String.format("cfgRead: %s", r))
                    );
                }),
                item("cfgSet", glasses -> {
                    glasses.cfgSet("DemoApp");
                }),
                item("cfgList", glasses -> {
                    glasses.cfgList(
                        r -> snack(String.format("cfgList: %s", Arrays.toString(r.toArray())))
                    );
                }),
                item("cfgRename(Bak)", glasses -> {
                    glasses.cfgRename("DemoApp", "DemoAppBak", 42);
                }),
                item("cfgRename", glasses -> {
                    glasses.cfgRename("DemoAppBak", "DemoApp", 42);
                }),
                item("cfgDelete", glasses -> {
                    glasses.cfgDelete("DemoApp");
                }),
                item("cfgDeleteLessUsed", glasses -> {
                    glasses.cfgDeleteLessUsed();
                }),
                item("cfgFreeSpace", glasses -> {
                    glasses.cfgFreeSpace(
                        r -> snack(String.format("cfgFreeSpace: %s", r))
                    );
                }),
                item("cfgGetNb", glasses -> {
                    glasses.cfgGetNb(
                        r -> snack(String.format("cfgGetNb: %s", r))
                    );
                }),
        };
    }

}
