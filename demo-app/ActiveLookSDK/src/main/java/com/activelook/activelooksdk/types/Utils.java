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

import java.security.InvalidParameterException;

public final class Utils {

    /**
     * Convert an int to a byte corresponding to a grey level.
     *
     * @param value The level.
     * @return The corresponding byte.
     * @raises InvalidParameterException If value if not between 0 and 15.
     */
    public static final byte greyLevel(int value) {
        Utils.constraints(value, 0, 15);
        return (byte) (value & 0xFF);
    }

    public static final short toSignedCoordinate(int value) {
        Utils.constraints(value, -128, 127);
        return (short) value;
    }

    /**
     * Split a byte array into chunk based on desired chunk size.
     * Incomplete chunk will stay incomplete (trimmed).
     *
     * @param source    The source buffer.
     * @param chunkSize the chunk size.
     * @return The array of chunk.
     */
    public static byte[][] split(byte[] source, int chunkSize) {
        byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunkSize)][];
        int start = 0;
        for (int i = 0; i < ret.length; i++) {
            byte[] chunk;
            if (start + chunkSize > source.length) {
                chunk = new byte[source.length - start];
                System.arraycopy(source, start, chunk, 0, source.length - start);
            } else {
                chunk = new byte[chunkSize];
                System.arraycopy(source, start, chunk, 0, chunkSize);
            }
            ret[i] = chunk;
            start += chunkSize;
        }
        return ret;
    }

    /*
    Helpers
     */
    protected static final void constraints(int value, int minValue, int maxValue) {
        if (value < minValue) {
            String msg = String.format("Value: %d < %d", value, minValue);
            throw new InvalidParameterException(msg);
        }
        if (value > maxValue) {
            String msg = String.format("Value: %d > %d", value, maxValue);
            throw new InvalidParameterException(msg);
        }
    }

    protected static final void constraintsMin(int value, int minValue) {
        if (value < minValue) {
            String msg = String.format("Value: %d < %d", value, minValue);
            throw new InvalidParameterException(msg);
        }
    }

    protected static final void constraintsMax(int value, int maxValue) {
        if (value > maxValue) {
            String msg = String.format("Value: %d > %d", value, maxValue);
            throw new InvalidParameterException(msg);
        }
    }

    interface PayloadElement {

        byte[] toBytes();
        default int length() {
            return this.toBytes().length;
        }

    }

}
