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

public class QueryId {

    private final byte value;

    public QueryId(byte value) {
        this.value = value;
    }

    public QueryId() {
        this((byte) 0);
    }

    byte[] toBytes() {
        return new byte[]{this.value};
    }

    public QueryId next() {
        return new QueryId((byte) ((this.value + 1) & 0xFF));
    }

    @Override
    public int hashCode() {
        return (int) this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryId)) return false;
        QueryId queryId = (QueryId) o;
        return value == queryId.value;
    }

    @Override
    public String toString() {
        return "QueryId{" +
                "value=" + value +
                '}';
    }

}
