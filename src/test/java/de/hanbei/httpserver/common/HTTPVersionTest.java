package de.hanbei.httpserver.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HTTPVersionTest {


    @Test
    public void testToString() {
        assertEquals("1.1", HTTPVersion.VERSION1_1.toString());
        assertEquals("1.0", HTTPVersion.VERSION1_0.toString());
        assertEquals("Unknown", HTTPVersion.UNKNOWN.toString());
    }

    @Test
    public void testParse() {
        assertEquals(HTTPVersion.VERSION1_1, HTTPVersion.parseString("1.1"));
        assertEquals(HTTPVersion.VERSION1_0, HTTPVersion.parseString("1.0"));
        assertEquals(HTTPVersion.UNKNOWN, HTTPVersion.parseString("1.2"));
    }


}
