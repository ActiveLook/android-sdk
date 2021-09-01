package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.SensorMode;
import com.activelook.activelooksdk.types.SensorParameters;

import java.util.Map;

public class OpticalSensorCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Optical sensor commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
                item("sensor(true)", glasses -> glasses.sensor(true)),
                item("sensor(false)", glasses -> glasses.sensor(false)),
                item("gesture(true)", glasses -> glasses.gesture(true)),
                item("gesture(false)", glasses -> glasses.gesture(false)),
                item("als(true)", glasses -> glasses.als(true)),
                item("als(false)", glasses -> glasses.als(false)),
        };
    }

}
