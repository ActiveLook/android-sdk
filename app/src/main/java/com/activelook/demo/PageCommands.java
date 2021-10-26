package com.activelook.demo;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.PageInfo;

import java.util.Arrays;
import java.util.Map;

public class PageCommands extends MainActivity2 {

    @Override
    protected String getCommandGroup() {
        return "Page commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        final PageInfo pi = new PageInfo((byte) 0x01)
                .addLayout((byte) 0x01, (short) 0, (byte) 0)
                .addLayout((byte) 0x02, (short) 50, (byte) 50)
        ;

        return new Map.Entry[]{
                item("clear", glasses -> {
                    glasses.clear();
                }),
                item("pageDisplay", glasses -> {
                    glasses.pageDisplay((byte) 0x01, new String [] { "Value1", "Value2"} );
                }),
                item("pageClear", glasses -> {
                    glasses.pageClear((byte) 0x01);
                }),
                item("pageSave", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.cfgSet("DemoApp");
                    glasses.pageSave(pi);
                }),
                item("pageDelete", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.cfgSet("DemoApp");
                    glasses.pageDelete((byte) 0x01);
                }),
                item("pageList", glasses -> {
                    glasses.cfgSet("DemoApp");
                    glasses.pageList(
                            r -> PageCommands.this.snack(String.format("pageList: %s", Arrays.toString(r.toArray())))
                    );
                }),
                item("pageGet", glasses -> {
                    glasses.cfgWrite("DemoApp", 1, 42);
                    glasses.cfgSet("DemoApp");
                    glasses.pageGet(
                            (byte) 0x01,
                            r -> PageCommands.this.snack(String.format("pageGet: %s", r))
                    );
                }),
        };
    }

}
