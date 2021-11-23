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
                item("clear()", glasses -> glasses.clear()),
                item("color(5)", glasses -> glasses.color((byte) 0x05)),
                item("color(10)", glasses -> glasses.color((byte) 0x0A)),
                item("points", glasses -> {
                    glasses.point(new Point(200, 200));
                    glasses.point(new Point(200, 210));
                    glasses.point(new Point(210, 210));
                    glasses.point(new Point(210, 200));
                    glasses.point(new Point(205, 205));
                }),
                item("line(0, 0, 304, 256)", glasses -> glasses.line(new Point(0, 0), new Point(304, 256))),
                item("rect(10, 10, 290, 240)", glasses -> glasses.rect(new Point(10, 10), new Point(290, 240))),
                item("rectf(13, 23, 20, 10)", glasses -> glasses.rectf(new Point(13, 23), new Point(20, 10))),
                item("circ(25, 25, 11)", glasses -> glasses.circ(new Point(25, 25), (byte) 11)),
                item("circf(25, 25, 7)", glasses -> glasses.circf(new Point(25, 25), (byte) 7)),
                item("txt(30, 30, 0, 1, 10, Bonjour)", glasses -> glasses.txt(new Point(30, 30), Rotation.TOP_LR,
                        (byte) 1,
                        (byte) 0x0A, "Bonjour")),
                item("polyline(3 pts)", glasses -> {
                    ArrayList<Point> pts = new ArrayList<>();
                    pts.add(new Point(50, 50));
                    pts.add(new Point(50, 200));
                    pts.add(new Point(250, 200));
                    glasses.polyline(pts);
                }),
                item("polyline(4 pts)", glasses -> {
                    ArrayList<Point> pts = new ArrayList<>();
                    pts.add(new Point(50, 50));
                    pts.add(new Point(50, 100));
                    pts.add(new Point(100, 100));
                    pts.add(new Point(100, 150));
                    glasses.polyline(pts);
                }),
        };
    }

}
