package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.Configuration;

import java.util.Map;

public class ConfigurationCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Graphics commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
                item("WConfigID(0x01, 0x00000000)",
                        glasses -> glasses.WConfigID(new Configuration((byte) 0x01, 0x00000000))
                ),
                item("RConfigID(0x01)",
                        glasses -> glasses.RConfigID((byte) 0x01, r -> {
                            ConfigurationCommands.this.snack(String.format("bmpList: %s", r.toString()));
                        })
                ),
                item("SetConfigID(0x01)",
                        glasses -> glasses.SetConfigID((byte) 0x01)
                ),
                item("SetConfigID(0x02)",
                        glasses -> glasses.SetConfigID((byte) 0x02)
                ),
        };
    }

}
