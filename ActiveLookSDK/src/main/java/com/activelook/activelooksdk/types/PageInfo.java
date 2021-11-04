/*

Copyright 2021 Microoled
Licensed under the Apache License, Version 2.0 (the “License”);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an “AS IS” BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.activelook.activelooksdk.types;

import com.activelook.activelooksdk.core.Command;

public class PageInfo {

    private final byte id;
    private final Command command;

    public PageInfo(byte id, byte[] layoutIds, short[] xs, byte [] ys) {
        this.id = id;
        this.command = new Command();
        this.command.addData(this.id);
        for(int i = 0; i<layoutIds.length; i++) {
            this.addLayout(layoutIds[i], xs[i], ys[i]);
        }
    }

    public PageInfo(byte id) {
        this(id, new byte [0], new short [0], new byte [0]);
    }

    public PageInfo(byte [] bytes) {
        this.id = bytes[0];
        this.command = new Command();
        this.command.addData(bytes);
    }

    public PageInfo addLayout(byte id, short x, byte y) {
        this.command.addData(id);
        this.command.addData(x);
        this.command.addData(y);
        return this;
    }

    public byte [] getPayload() {
        return this.command.getData();
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "id=" + id +
                ", command=" + Command.bytesToStr(this.command.getData()) +
                '}';
    }

}
