package de.hanbei.httpserver;

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


import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.response.Response;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class MockHttpServerTest {

    private MockHttpServer httpServer;
    private HttpClient httpclient;

    @Before
    public void setUp() throws Exception {
        this.httpServer = new MockHttpServer(7001);
        this.httpServer.start();
        httpServer.addResponse(Method.GET, new URI("/test"), Response.ok().build());
        httpServer.addResponse(Method.GET, new URI("/test3"), Response.ok().content("TestContent").build());
        httpServer.addResponse(Method.GET, new URI("/test2"), Response.status(Status.NOT_FOUND).build());
        assertTrue(this.httpServer.isRunning());

        httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager());
    }

    @Test
    public void testCall() throws IOException {
        HttpGet httpget = new HttpGet("http://localhost:7001/test");
        HttpResponse response = httpclient.execute(httpget);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testTrailingSlashIsTheSameAsWithout() throws IOException {
        HttpGet httpget = new HttpGet("http://localhost:7001/test");
        HttpResponse response = httpclient.execute(httpget);
        assertEquals("non trailing slash broken", 200, response.getStatusLine().getStatusCode());

        HttpGet httpget2 = new HttpGet("http://localhost:7001/test/");
        HttpResponse response2 = httpclient.execute(httpget2);
        assertEquals("Trailing slash broken", 200, response2.getStatusLine().getStatusCode());
    }

    @Test
    public void testCall2() throws IOException {
        HttpGet httpget = new HttpGet("http://localhost:7001/test2");
        HttpResponse response = httpclient.execute(httpget);
        assertEquals(404, response.getStatusLine().getStatusCode());
    }

    @Test
    public void getContent() throws IOException {
        HttpGet httpget = new HttpGet("http://localhost:7001/test3");
        HttpResponse response = httpclient.execute(httpget);
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(11, response.getEntity().getContentLength());
    }

    @Test(expected = NoHttpResponseException.class)
    public void testTimeout() throws IOException {
        httpServer.setTimeout(true);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://localhost:7001/timeout");
        httpclient.execute(httpget);
    }

    @Test
    public void testIsRunning() {
        this.httpServer.stop();
        assertFalse(this.httpServer.isRunning());
    }

    @After
    public void tearDown() throws Exception {
        this.httpServer.stop();
    }

}
