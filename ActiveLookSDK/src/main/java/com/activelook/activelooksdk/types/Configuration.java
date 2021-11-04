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

public class Configuration {

    private final byte number;
    private final int id;

    public Configuration(byte number, int id) {
        this.number = number;
        this.id = id;
    }

    public Configuration(byte[] bytes) {
        this.number = bytes[0];
        this.id = (bytes[1] << 24) | (bytes[2] << 16) | (bytes[3] << 8) | (bytes[4]);
    }

    public byte getNumber() {
        return this.number;
    }

    public int getId() {
        return this.id;
    }

    public byte[] toBytes() {
        return new Command().addData(this.number).addData(this.id).addData(new byte[]{0x00, 0x00, 0x00}).getData();
    }

}
