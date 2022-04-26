/*
 * Copyright (c) 2022 Microoled.
 * Licensed under the Apache License, Version 2.0 (the “License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.activelook.internal

import android.os.Parcelable
import com.activelook.activelooksdk.SerializedGlasses
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SimpleSerializedGlasses(val gAddress: String, val gManufacturer: String, val gName: String) :
    SerializedGlasses, Parcelable {
    override fun getAddress(): String {
        return this.gAddress
    }

    override fun getManufacturer(): String {
        return this.gManufacturer
    }

    override fun getName(): String {
        return this.gName
    }
}
