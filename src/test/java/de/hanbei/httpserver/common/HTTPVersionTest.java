package de.hanbei.httpserver.common;

import org.apache.http.protocol.HTTP;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 24.04.11
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */
public class HTTPVersionTest {

    @Before
    public void setup() {

    }

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
