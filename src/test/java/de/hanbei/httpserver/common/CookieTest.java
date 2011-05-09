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
