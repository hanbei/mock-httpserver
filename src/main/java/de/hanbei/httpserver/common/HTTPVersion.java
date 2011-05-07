package de.hanbei.httpserver.common;

/**
 * Enum to represent HTTP Versions.
 */
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
