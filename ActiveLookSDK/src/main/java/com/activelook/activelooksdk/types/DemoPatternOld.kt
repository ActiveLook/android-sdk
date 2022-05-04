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
package com.activelook.activelooksdk.types

import com.activelook.activelooksdk.types.Utils.FieldWithValue

enum class DemoPatternOld : FieldWithValue {
    FILL {
        override fun toBytes(): ByteArray {
            return byteArrayOf(0x00.toByte())
        }
    },
    CROSS {
        override fun toBytes(): ByteArray {
            return byteArrayOf(0x01.toByte())
        }
    },
    IMAGE {
        override fun toBytes(): ByteArray {
            return byteArrayOf(0x02.toByte())
        }
    }
}