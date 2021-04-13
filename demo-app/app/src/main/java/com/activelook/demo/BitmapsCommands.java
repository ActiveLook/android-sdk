package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.Image1bppData;
import com.activelook.activelooksdk.types.ImageData;

import java.util.Arrays;
import java.util.Map;

public class BitmapsCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Bitmaps commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        ImageData img1 = new ImageData((char) 15, new byte[]{
                (byte) 0x11, (byte) 0x32, (byte) 0x43, (byte) 0x55, (byte) 0x76, (byte) 0x88, (byte) 0xA9,
                (byte) 0x0A, (byte) 0x21, (byte) 0x32, (byte) 0x44, (byte) 0x65, (byte) 0x77, (byte) 0x98,
                (byte) 0xA9, (byte) 0x0B, (byte) 0x21, (byte) 0x43, (byte) 0x54, (byte) 0x66, (byte) 0x87,
                (byte) 0x98, (byte) 0xBA, (byte) 0x0B, (byte) 0x32, (byte) 0x43, (byte) 0x55, (byte) 0x76,
                (byte) 0x88, (byte) 0xA9, (byte) 0xBA, (byte) 0x0C, (byte) 0x32, (byte) 0x44, (byte) 0x65,
                (byte) 0x77, (byte) 0x98, (byte) 0xA9, (byte) 0xBB, (byte) 0x0C, (byte) 0x43, (byte) 0x54,
                (byte) 0x66, (byte) 0x87, (byte) 0x98, (byte) 0xBA, (byte) 0xCB, (byte) 0x0D, (byte) 0x43,
                (byte) 0x65, (byte) 0x76, (byte) 0x88, (byte) 0xA9, (byte) 0xBA, (byte) 0xCC, (byte) 0x0D,
                (byte) 0x44, (byte) 0x65, (byte) 0x77, (byte) 0x98, (byte) 0xA9, (byte) 0xBB, (byte) 0xDC,
                (byte) 0x0E, (byte) 0x54, (byte) 0x66, (byte) 0x87, (byte) 0x98, (byte) 0xBA, (byte) 0xCB,
                (byte) 0xDD, (byte) 0x0E, (byte) 0x65, (byte) 0x76, (byte) 0x88, (byte) 0xA9, (byte) 0xBA,
                (byte) 0xCC, (byte) 0xED, (byte) 0x0E,
        });
        Image1bppData img2 = new Image1bppData((char) 15, new byte [] {
                (byte) 0xC0, (byte) 0x01, (byte) 0x30, (byte) 0x06, (byte) 0x08, (byte) 0x08, (byte) 0x04,
                (byte) 0x10, (byte) 0x02, (byte) 0x20, (byte) 0x01, (byte) 0x40, (byte) 0x01, (byte) 0x40,
                (byte) 0x81, (byte) 0x40, (byte) 0x62, (byte) 0x21, (byte) 0x1C, (byte) 0x1E,
        });
        return new Map.Entry[]{
                item("bmpList", glasses -> glasses.imgList(r -> {
                    BitmapsCommands.this.snack(String.format("bmpList: %s", Arrays.toString(r.toArray())));
                })),
                item("saveBmp", glasses -> glasses.imgSave(img1)),
                item("bitmap", glasses -> glasses.imgDisplay((byte) 0x01, (short) 10, (short) 50)),
                item("eraseBmp", glasses -> glasses.imgDelete((byte) 0x01)),
                item("streamBitmap", glasses -> glasses.imgStream(img2, (short) 20, (short) 30)),
                item("saveBitmap", glasses -> glasses.imgSave1bpp(img2)),
        };
    }

}
