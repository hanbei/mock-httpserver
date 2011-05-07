package de.hanbei.httpserver.common;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CookieTest {

    private Cookie cookie;
    private Cookie cookieNotEquals;
    private Cookie cookieEquals;

    @Before
    public void createCookies() {
        cookie = new Cookie();
        cookie.setName("testCookie");
        cookie.setValue("testCookieValue");

        cookieNotEquals = new Cookie();
        cookieNotEquals.setName("testCookie2");
        cookieNotEquals.setValue("testCookie2Value");

        cookieEquals = new Cookie();
        cookieEquals.setName("testCookie");
        cookieEquals.setValue("testCookieValue");
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(cookie.equals(cookieEquals));
        assertFalse(cookie.equals(cookieNotEquals));
        assertTrue(cookie.equals(cookie));
    }

    @Test
    public void testHashCode() throws Exception {
        assertTrue(cookie.hashCode() == cookieEquals.hashCode());
        assertTrue(cookie.hashCode() != cookieNotEquals.hashCode());
        assertTrue(cookie.hashCode() == cookie.hashCode());
    }
}
