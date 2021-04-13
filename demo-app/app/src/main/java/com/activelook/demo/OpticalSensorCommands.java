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
                item("setSensorParameters(ALS_ARRAY, [1,2,3,4,5,6,7,8,9])", glasses -> {
                    SensorParameters sp = new SensorParameters();
                    sp.setAlsLuma(new short [] {1, 2, 3, 4, 5, 6, 7, 8, 9});
                    glasses.setSensorParameters(SensorMode.ALS_ARRAY, sp);
                }),
                item("setSensorParameters(ALS_ARRAY, [9,8,7,6,5,4,3,2,1])", glasses -> {
                    SensorParameters sp = new SensorParameters();
                    sp.setAlsLuma(new short [] {9, 8, 7, 6, 5, 4, 3, 2, 1});
                    glasses.setSensorParameters(SensorMode.ALS_ARRAY, sp);
                }),
                item("setSensorParameters(ALS_PERIOD, 3)", glasses -> {
                    SensorParameters sp = new SensorParameters();
                    sp.setAlsPeriod((short) 3);
                    glasses.setSensorParameters(SensorMode.ALS_PERIOD, sp);
                }),
                item("setSensorParameters(ALS_PERIOD, 9)", glasses -> {
                    SensorParameters sp = new SensorParameters();
                    sp.setAlsPeriod((short) 9);
                    glasses.setSensorParameters(SensorMode.ALS_PERIOD, sp);
                }),
                item("setSensorParameters(GESTURE_PERIOD, 4)", glasses -> {
                    SensorParameters sp = new SensorParameters();
                    sp.setGesturePeriod((short) 3);
                    glasses.setSensorParameters(SensorMode.GESTURE_PERIOD, sp);
                }),
                item("setSensorParameters(GESTURE_PERIOD, 7)", glasses -> {
                    SensorParameters sp = new SensorParameters();
                    sp.setGesturePeriod((short) 9);
                    glasses.setSensorParameters(SensorMode.GESTURE_PERIOD, sp);
                }),
                item("getSensorParameters()", glasses -> {
                    glasses.getSensorParameters(r -> {
                        OpticalSensorCommands.this.snack(String.format("Sensor parameters: %s", r));
                    });
                }),
        };
    }

}
