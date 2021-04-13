package com.activelook.demo;

import android.graphics.Point;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.Rotation;

import java.util.ArrayList;
import java.util.Map;

public class GraphicsCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Graphics commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{
                item("color(5)", glasses -> glasses.color((byte) 0x05)),
                item("color(10)", glasses -> glasses.color((byte) 0x0A)),
                item("point(10, 20)", glasses -> glasses.point(new Point(10, 20))),
                item("line(11, 21, 20, 10)", glasses -> glasses.line(new Point(11, 21), new Point(20, 10))),
                item("rect(12, 22, 20, 10)", glasses -> glasses.rect(new Point(12, 22), new Point(20, 10))),
                item("rectf(13, 23, 20, 10)", glasses -> glasses.rectf(new Point(13, 23), new Point(20, 10))),
                item("circ(25, 25, 11)", glasses -> glasses.circ(new Point(25, 25), (byte) 11)),
                item("circf(25, 25, 7)", glasses -> glasses.circf(new Point(25, 25), (byte) 7)),
                item("txt(30, 30, 0, 1, 10, Bonjour)", glasses -> glasses.txt(new Point(30, 30), Rotation.TOP_LR,
                        (byte) 1,
                        (byte) 0x0A, "Bonjour")),
                item("polyline(...)", glasses -> {
                    ArrayList<Point> pts = new ArrayList<>();
                    pts.add(new Point(100, 100));
                    pts.add(new Point(200, 200));
                    pts.add(new Point(100, 200));
                    pts.add(new Point(300, 100));
                    glasses.polyline(pts);
                }),
        };
    }

}
