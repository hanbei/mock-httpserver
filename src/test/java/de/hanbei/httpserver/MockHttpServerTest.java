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
package de.hanbei.httpserver;

import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
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
        httpServer.addResponse(Method.GET, new URI("/test4"), Response.ok().content(new byte[]{1, 2, 3, 4, 5}).build());
        httpServer.addResponse(Method.GET, new URI("/testUtf8"),
                Response.ok().content("Cæelo", Charsets.ISO_8859_1).type("text/plain; charset=iso-8859-1").build());

        httpServer.addResponse(Method.POST, new URI("/post2"), Response.ok().build());

        httpServer.addRequestProcessor(Method.POST, URI.create("post"), new RequestProcessor() {
            @Override
            public Response process(Request request) {
                System.err.println("Request received.");
                if (request.getContent().getContentAsString().equals("Test")) {
                    return Response.ok().build();
                } else {
                    return Response.status(Status.UNAUTHORIZED).build();
                }
            }
        });
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
    public void getContentUTF8Encoding() throws IOException {
        HttpGet httpget = new HttpGet("http://localhost:7001/testUtf8");
        httpget.setHeader("Accept-Charset", Charsets.ISO_8859_1.name());
        HttpResponse response = httpclient.execute(httpget);
        assertEquals(200, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        String content = IOUtils.toString(entity.getContent(), Charsets.ISO_8859_1);
        assertEquals("Cæelo", content.trim());
    }

    @Test
    public void getContentAsBytes() throws IOException {
        HttpGet httpget = new HttpGet("http://localhost:7001/test4");
        HttpResponse response = httpclient.execute(httpget);
        assertEquals(200, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        IOUtils.copy(entity.getContent(), bytes);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, bytes.toByteArray());
    }

    @Test
    public void testDefaultResponse() throws IOException {
        HttpGet httpget = new HttpGet("http://localhost:7001/something");
        HttpResponse response = httpclient.execute(httpget);
        assertEquals(404, response.getStatusLine().getStatusCode());

        httpServer.setDefaultResponse(Response.status(Status.INTERNAL_SERVER_ERROR).build());
        HttpGet httpget2 = new HttpGet("http://localhost:7001/default");
        HttpResponse response2 = httpclient.execute(httpget2);
        assertEquals(500, response2.getStatusLine().getStatusCode());
    }

    @Test
    public void testRequestProcessor() throws IOException {
        HttpPost httpPost = new HttpPost("http://localhost:7001/post");
        HttpResponse response = httpclient.execute(httpPost);
        assertEquals(401, response.getStatusLine().getStatusCode());

        HttpPost httpPost2 = new HttpPost("http://localhost:7001/post");
        httpPost2.setEntity(new StringEntity("Test"));
        HttpResponse response2 = httpclient.execute(httpPost2);
        assertEquals(200, response2.getStatusLine().getStatusCode());
    }

    @Test
    public void testUrlEncodedFormEntityFailsPostPredefinedResponse() throws IOException {
        HttpPost httpPost3 = new HttpPost("http://localhost:7001/post2");
        httpPost3.setEntity(new UrlEncodedFormEntity(Collections.<NameValuePair>singletonList(new BasicNameValuePair("test", "abc"))));
        HttpResponse response3 = httpclient.execute(httpPost3);
        assertEquals(200, response3.getStatusLine().getStatusCode());
    }

    /**
     * Test for issue #8. Encoding was not set.
     * @throws IOException
     */
    @Test
    public void testUrlEncodedFormEntityFailsPostRequestProcessor() throws IOException {
        HttpPost httpPost3 = new HttpPost("http://localhost:7001/post");
        httpPost3.setEntity(new UrlEncodedFormEntity(Collections.<NameValuePair>singletonList(new BasicNameValuePair("test", "abc"))));
        HttpResponse response3 = httpclient.execute(httpPost3);
        assertEquals(401, response3.getStatusLine().getStatusCode());
    }

    @Test
    public void testIsRunning() {
        assertTrue(this.httpServer.isRunning());
        this.httpServer.stop();
        assertFalse(this.httpServer.isRunning());
    }

    @Test
    public void testConnectionProblem() throws IOException {
        for (int i = 0; i < 3; i++) {
            // first request
            HttpGet httpget1 = new HttpGet("http://localhost:7001/test3");
            HttpResponse response1 = this.httpclient.execute(httpget1);
            assertEquals(200, response1.getStatusLine().getStatusCode());
            EntityUtils.consume(response1.getEntity());

            // second request
            HttpGet httpget2 = new HttpGet("http://localhost:7001/test3");
            HttpResponse response2 = this.httpclient.execute(httpget2);
            assertEquals(200, response2.getStatusLine().getStatusCode());
            EntityUtils.consume(response2.getEntity());
        }
    }

    @After
    public void tearDown() throws Exception {
        this.httpServer.stop();
    }

}
