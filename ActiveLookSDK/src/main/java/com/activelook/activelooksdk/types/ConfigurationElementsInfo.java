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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationElementsInfo {

    private final int version;
    private final byte nbImg;
    private final byte nbLayout;
    private final byte nbFont;
    private final byte nbPage;
    private final byte nbGauge;

    public ConfigurationElementsInfo(byte[] payload) {
        this.version = ((payload[0] << 24) |(payload[1] << 16) |(payload[2] << 8) | payload[3]);
        this.nbImg = payload[4];
        this.nbLayout = payload[5];
        this.nbFont = payload[6];
        this.nbPage = payload[7];
        this.nbGauge = payload[8];
    }

    @Override
    public String toString() {
        return "ConfigurationElementsInfo{" +
                "version=" + version +
                ", nbImg=" + nbImg +
                ", nbLayout=" + nbLayout +
                ", nbFont=" + nbFont +
                ", nbPage=" + nbPage +
                ", nbGauge=" + nbGauge +
                '}';
    }

}
