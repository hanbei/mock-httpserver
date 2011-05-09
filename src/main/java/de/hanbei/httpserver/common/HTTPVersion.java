/* Copyright 2011 Florian Schulz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package de.hanbei.httpserver.common;

/** Enum to represent HTTP Versions. */
public enum HTTPVersion {

    VERSION1_0,
    VERSION1_1,
    UNKNOWN;

    public static HTTPVersion parseString(String version) {
        if ("1.1".equals(version)) {
            return VERSION1_1;
        }
        if ("1.0".equals(version)) {
            return VERSION1_0;
        }
        return UNKNOWN;
    }

    public String toString() {
        if (this.equals(VERSION1_1)) {
            return "1.1";
        }
        if (this.equals(VERSION1_0)) {
            return "1.0";
        }
        return "Unknown";
    }
}
