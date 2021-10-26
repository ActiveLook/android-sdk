package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.DemoPattern;
import com.activelook.activelooksdk.types.LedState;

import java.util.Map;

public class GeneralCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "General commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
                item("power(true)", glasses -> glasses.power(true)),
                item("power(false)", glasses -> glasses.power(false)),
                item("clear()", glasses -> glasses.clear()),
                item("grey(b0x03)", glasses -> glasses.grey((byte) 0x03)),
                item("grey(b0x07)", glasses -> glasses.grey((byte) 0x07)),
                item("grey(b0x0B)", glasses -> glasses.grey((byte) 0x0B)),
                item("grey(b0x0F)", glasses -> glasses.grey((byte) 0x0F)),
                item("test(CROSS)", glasses -> glasses.demo(DemoPattern.CROSS)),
                item("test(FILL)", glasses -> glasses.demo(DemoPattern.FILL)),
                item("test(IMAGE)", glasses -> glasses.demo(DemoPattern.IMAGE)),
                item("battery()", glasses -> glasses.battery(r -> {
                    GeneralCommands.this.snack(String.format("Battery level: %d", r));
                })),
                item("vers()", glasses -> glasses.vers(r -> {
                    GeneralCommands.this.snack(String.format("Version: %s", r));
                })),
                item("led(OFF)", glasses -> glasses.led(LedState.OFF)),
                item("led(ON)", glasses -> glasses.led(LedState.ON)),
                item("led(TOGGLE)", glasses -> glasses.led(LedState.TOGGLE)),
                item("led(BLINK)", glasses -> glasses.led(LedState.BLINK)),
                item("shift(0, 0)", glasses -> glasses.shift((short) 0, (short) 0)),
                item("shift(50, 0)", glasses -> glasses.shift((short) 50, (short) 0)),
                item("shift(-50, 0)", glasses -> glasses.shift((short) -50, (short) 0)),
                item("shift(0, 50)", glasses -> glasses.shift((short) 0, (short) 50)),
                item("shift(0, -50)", glasses -> glasses.shift((short) 0, (short) -50)),
                item("settings()", glasses -> glasses.settings(r -> {
                    GeneralCommands.this.snack(String.format("settings: %s", r));
                }))
        };
    }

}
