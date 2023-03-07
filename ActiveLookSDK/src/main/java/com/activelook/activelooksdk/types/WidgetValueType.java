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

 public enum WidgetValueType implements Utils.FieldWithValue {
     WIDGET_VAL_TYPE_TEXT {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x00};
        }
    },
     WIDGET_VAL_TYPE_NUMBER {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x01};
        }
    },
     WIDGET_VAL_TYPE_DUR_HMS {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x02};
        }
    },
     WIDGET_VAL_TYPE_DUR_HM {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x03};
        }
    },
     WIDGET_VAL_TYPE_DUR_MS {
        @Override
        public byte[] toBytes() {
            return new byte[]{(byte) 0x04};
        }
    }





     }
