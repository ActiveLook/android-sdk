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

 public enum ImgSaveFormat implements Utils.FieldWithValue {
    /** Plain 4bpp, one nibble per pixel, no compression (doc section 5.5.1). An odd-width row
     * needs a dummy trailing pixel to fill its last byte. */
    MONO_4BPP {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x00};
        }
    },
    /** Plain 1bpp, one bit per pixel, no compression (doc section 5.5.2). */
    MONO_1BPP {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x01};
        }
    },
    /** 4bpp with Heatshrink compression, decompressed into 4bpp by the firmware before saving
     * (doc section 5.5.3) -- reduces BLE transfer time without changing display-time cost. */
    MONO_4BPP_HEATSHRINK {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x02};
        }
    },
    /** 4bpp with Heatshrink compression, stored compressed and decompressed into 4bpp only at
     * display time (doc section 5.5.3) -- reduces BLE transfer and flash usage at the cost of
     * display time. */
    MONO_4BPP_HEATSHRINK_SAVE_COMP {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x03};
        }
    },
     /** 4bpp grey + 4-bit alpha channel (doc section 5.5.4): one byte per pixel,
      * high nibble = grey level (0-15), low nibble = alpha (0-15, 0x0 fully transparent, 0xF fully opaque).
      * Sent uncompressed -- no Heatshrink variant exists for this format. */
     MONO_4BPP_ALPHA {
         @Override
         public byte[] toBytes() {
             return new byte[]{(byte) 0x08};
         }
     },
     /** 8bpp RG (red-green) color (doc section 5.5.5): color-glasses-only, 81 colors (9 red x 9
      * green intensities, no blue subpixel). Sent Heatshrink-compressed. */
     RG_COLOR_8BPP {
         @Override
         public byte[] toBytes() {
             return new byte[]{(byte) 0x0A};
         }
     },
     /** 8bpp RG color plus 4-bit alpha per pixel (doc section 5.5.6): color-glasses-only, same
      * 81-color RG palette as RG_COLOR_8BPP. Pixels are grouped in pairs -- color, color, alpha
      * byte (low nibble = first pixel's alpha, high nibble = second's) -- and firmware only
      * supports boolean alpha (0 or 15; any nonzero value reads as fully opaque). Sent
      * Heatshrink-compressed, same as RG_COLOR_8BPP. */
     RG_COLOR_8BPP_ALPHA {
         @Override
         public byte[] toBytes() {
             return new byte[]{(byte) 0x0C};
         }
     },

}
