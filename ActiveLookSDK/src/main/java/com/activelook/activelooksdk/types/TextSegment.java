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

public class TextSegment {

    private  byte font;
    private  byte color;
    private  String string;

    public TextSegment() {
        this.string = "";
        this.font = 0;
        this.color = 15;
    }

    public TextSegment(String string) {
        this.string = string;
        this.font = 0;
        this.color = 15;
    }

    public TextSegment(String string, byte font) {
        this.string = string;
        this.font = font;
        this.color = 15;
    }

    public TextSegment(String string, byte font, byte color) {
        this.string = string;
        this.font = font;
        this.color = color;
    }

    public byte getFont() {
        return this.font;
    }

    public byte getColor() {
        return this.color;
    }

    public String getString() {
        return this.string;
    }

}
