package de.hanbei.httpserver;

import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.response.Response;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

    @Before
    public void setUp() throws Exception {
        this.httpServer = new MockHttpServer();
        this.httpServer.setPort(7001);
        this.httpServer.start();
        httpServer.addResponse(new URI("/test"), Response.ok().build());
        httpServer.addResponse(new URI("/test2"), Response.status(Status.NOT_FOUND).build());
        assertTrue(this.httpServer.isRunning());
    }

    @Test
    public void testHandlerCalled() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://localhost:7001/test");
        HttpResponse response = httpclient.execute(httpget);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testHandlerCalled2() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://localhost:7001/test2");
        HttpResponse response = httpclient.execute(httpget);
        assertEquals(404, response.getStatusLine().getStatusCode());
    }


    @Test(expected = NoHttpResponseException.class)
    public void testTimeout() throws IOException {
        httpServer.setTimeout(true);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://localhost:7001/timeout");
        HttpResponse response = httpclient.execute(httpget);
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
