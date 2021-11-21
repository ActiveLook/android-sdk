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
package com.activelook.activelooksdk.core;

import android.util.Log;

import java.nio.charset.StandardCharsets;

public class Payload {

    private final CommandData bytes;
    private final byte commandId;
    private byte[] queryId;
    // private byte[] data;

    public Payload() {
        super();
        this.commandId = (byte) 0x00;
        this.bytes = new CommandData();
    }

    Payload(byte commandId) {
        super();
        this.commandId = commandId;
        this.bytes = new CommandData();
    }

    Payload(byte commandId, byte[] queryId) {
        super();
        this.commandId = commandId;
        this.queryId = queryId;
        this.bytes = new CommandData();
    }

    public Payload(byte[] payload) {
        super();
        int fullLength = 5;
        assert payload[0] == (byte) 0xFF;
        this.commandId = payload[1];
        int n = payload[2] & (byte) 0x0F;
        int offset = 4;
        if ((payload[2] & 0x10) == 0x10) {
            fullLength = (payload[3] << 8) | payload[4];
            offset++;
        } else {
            fullLength = payload[3];
        }
        int m = fullLength - (5 + n);
        if (n > 0) {
            this.queryId = new byte[n];
            System.arraycopy(payload, offset, this.queryId, 0, n);
        }
        this.bytes = new CommandData();
        if (m > 0) {
            final byte [] data = new byte[m];
            System.arraycopy(payload, offset + n, data, 0, m);
            this.bytes.add(data);
        }
        assert payload[fullLength - 1] == (byte) 0xAA;
    }

    public static final boolean isValidBuffer(byte[] payload) {
        Log.d("Validating", Payload.bytesToStr(payload));
        int fullLength = 5;
        assert payload[0] == (byte) 0xFF;
        if ((payload[2] & 0x10) == 0x10) {
            fullLength = (payload[3] << 8) | payload[4];
        } else {
            fullLength = payload[3];
        }
        if (payload.length == fullLength) {
            assert payload[fullLength - 1] == (byte) 0xAA;
            return true;
        } else {
            return false;
        }
    }

    public static final String bytesToStr(byte[] payload) {
        if (payload == null) {
            return "[]";
        }
        StringBuilder result = new StringBuilder();
        String prefix = "[ ";
        for (byte aByte : payload) {
            result.append(String.format("%s0x%02X", prefix, aByte));
            prefix = ", ";
        }
        return String.format("%s ]", result.toString());
    }

    /**
     * A command includes a header and a footer.
     * The header is made of START + Command ID + Command Format + length (+ Query ID).
     * The command layout is as follows:
     * | 0xFF   | 0x..       | 0x0n           | 0x..        | n * 0x..  | m * 0x..       | 0xAA   |
     * |--------|------------|----------------|-------------|-----------|----------------|--------|
     * | HEADER | Command ID | Command Format | data length | Query ID  | Data           | FOOTER |
     * | 1B     | 1B         | 1B             | 1B          | nB        | mB             | 1B     |
     * Or
     * | 0xFF   | 0x..       | 0x1n           | 0x.. 0x..   | n * 0x..  | m * 0x..       | 0xAA   |
     * |--------|------------|----------------|-------------|-----------|----------------|--------|
     * | HEADER | Command ID | Command Format | data length | Query ID  | Data           | FOOTER |
     * | 1B     | 1B         | 1B             | 2B          | nB        | mB             | 1B     |
     *
     * @param commandId: Command identifier
     * @param queryId:   Can be null: Defines by the user, allows to identify a command (query) and the associated
     *                   response. When a queryId is sent with the command, the response includes this queryId, allowing
     *                   the user to match the response with the original command in an asynchronous system.
     * @param data:      Can be null. Parameters of the command
     * @return the byte buffer to write
     */
    private static final byte[] getBytes(byte commandId, byte[] queryId, byte[] data) {
        int n = queryId.length;
        int m = data.length;
        assert n <= 15 : String.format("QueryId length too big: %d > 15", n);
        assert m <= 512 : String.format("Data length too big: %d > 512", m);
        int fullLength = 5 + n + m;
        int copyOffset = 4;
        if (fullLength > 0xFF) {
            fullLength += 1;
            copyOffset = 5;
        }
        byte[] payload = new byte[fullLength];
        payload[0] = (byte) 0xFF;
        payload[1] = commandId;
        if (fullLength > 0xFF) {
            payload[2] = (byte) (0x10 | n);
            payload[3] = (byte) ((fullLength & 0xFF00) >> 8);
            payload[4] = (byte) (fullLength & 0x00FF);
        } else {
            payload[2] = (byte) (0x00 | n);
            payload[3] = (byte) fullLength;
        }
        System.arraycopy(queryId, 0, payload, copyOffset, n);
        copyOffset += n;
        System.arraycopy(data, 0, payload, copyOffset, m);
        copyOffset += m;
        payload[copyOffset] = (byte) 0xAA;
        return payload;
    }

    private static final byte[] getBytesWithData(byte commandId, byte[] data) {
        int m = data.length;
        assert m <= 512 : String.format("Data length too big: %d > 512", m);
        int fullLength = 5 + m;
        int copyOffset = 4;
        if (fullLength > 0xFF) {
            fullLength += 1;
            copyOffset = 5;
        }
        byte[] payload = new byte[fullLength];
        payload[0] = (byte) 0xFF;
        payload[1] = commandId;
        if (fullLength > 0xFF) {
            payload[2] = (byte) 0x10;
            payload[3] = (byte) ((fullLength & 0xFF00) >> 8);
            payload[4] = (byte) (fullLength & 0x00FF);
        } else {
            payload[2] = (byte) 0x00;
            payload[3] = (byte) fullLength;
        }
        System.arraycopy(data, 0, payload, copyOffset, m);
        copyOffset += m;
        payload[copyOffset] = (byte) 0xAA;
        return payload;
    }

    private static final byte[] getBytesWithQueryId(byte commandId, byte[] queryId) {
        int n = queryId.length;
        assert n <= 15 : String.format("QueryId length too big: %d > 15", n);
        int fullLength = 5 + n;
        int copyOffset = 4;
        byte[] payload = new byte[fullLength];
        payload[0] = (byte) 0xFF;
        payload[1] = commandId;
        payload[2] = (byte) (0x00 | n);
        payload[3] = (byte) fullLength;
        System.arraycopy(queryId, 0, payload, copyOffset, n);
        copyOffset += n;
        payload[copyOffset] = (byte) 0xAA;
        return payload;
    }

    private static final byte[] combine(byte[]... parameters) {
        int fullLength = 0;
        for (byte[] parameter : parameters) {
            fullLength += parameter.length;
        }
        byte[] result = new byte[fullLength];
        int offset = 0;
        for (byte[] parameter : parameters) {
            System.arraycopy(parameter, 0, result, offset, parameter.length);
            offset += parameter.length;
        }
        return result;
    }

    private static final byte[] getBytes(byte commandId) {
        return new byte[]{(byte) 0xFF, commandId, (byte) 0x00, (byte) 0x05, (byte) 0xAA};
    }

    @Override
    public String toString() {
        return "Payload{" +
                "commandId=" + this.commandId +
                ", queryId=" + bytesToStr(this.queryId) +
                ", data=" + bytesToStr(this.bytes.getBytes()) +
                '}';
    }

    /*
     * Private helpers
     */

    public QueryId getQueryId() {
        if (this.queryId != null) {
            return new QueryId(this.queryId[0]);
        } else {
            return null;
        }
    }

    public void setQueryId(byte[] queryId) {
        this.queryId = queryId;
    }

    public void setQueryId(QueryId qid) {
        this.setQueryId(qid.toBytes());
    }

    public byte[] getData() {
        return this.bytes.getBytes();
    }

    public byte[] toBytes() {
        if (this.queryId == null) {
            return getBytesWithData(this.commandId, this.bytes.getBytes());
        } else {
            return getBytes(this.commandId, this.queryId, this.bytes.getBytes());
        }
    }

    public Payload addData(byte[] bytes) {
        this.bytes.add(bytes);
        return this;
    }

    public Payload addData(byte value) {
        return this.addData(new byte[]{value});
    }

    public Payload addBytes(byte ...bytes) {
        this.bytes.add(bytes);
        return this;
    }

    public Payload addByte(byte value) {
        return this.addBytes(value);
    }

    public Payload addByte(boolean value) {
        return this.addBytes(value ? (byte) 0x01 : (byte) 0x00);
    }

    public Payload addData(short[] values) {
        for (short value : values) {
            this.addData(new byte[]{(byte) ((value & 0xFF00) >> 8), (byte) (value & 0x00FF)});
        }
        return this;
    }

    public Payload addData(char[] values) {
        for (char value : values) {
            this.addData(new byte[]{(byte) ((value & 0xFF00) >> 8), (byte) (value & 0x00FF)});
        }
        return this;
    }

    public Payload addData(short value) {
        return this.addData(new short[]{value});
    }

    public Payload addData(char value) {
        return this.addData(new char[]{value});
    }

    public Payload addData(String value) {
        return this.addData(value.getBytes(StandardCharsets.US_ASCII));
    }

    public Payload addData(String value, boolean nulTerminated) {
        if (nulTerminated == true) {
            return this.addData(value.getBytes(StandardCharsets.US_ASCII)).addData((byte) 0x00);
        }
        return this.addData(value.getBytes(StandardCharsets.US_ASCII));
    }

    public void addData(String value, int size, boolean nulTerminated) {
        byte[] buffer = new byte[size];
        byte[] strBuffer = value.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(strBuffer, 0, buffer, 0, Math.min(buffer.length, strBuffer.length));
        if (nulTerminated == true) {
            buffer[buffer.length - 1] = (byte) 0x00;
        }
        this.addData(buffer);
    }

    public Payload addData(int value) {
        byte b0 = (byte) ((value & 0xFF000000) >> 24);
        byte b1 = (byte) ((value & 0x00FF0000) >> 16);
        byte b2 = (byte) ((value & 0x0000FF00) >> 8);
        byte b3 = (byte) (value & 0x000000FF);
        return this.addData(new byte[]{b0, b1, b2, b3});
    }

}
