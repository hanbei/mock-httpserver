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
package de.hanbei.httpserver.request;

import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import de.hanbei.httpserver.common.*;
import org.junit.Before;
import org.junit.Test;

public class RequestParserTest {

    private static final String REQUEST = "POST /test/uri/ HTTP/1.1\r\nHost: localhost:8079\r\n"
            + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"
            + "Accept-Language: en-us,en;q=0.5\r\n"
            + "Accept-Encoding: gzip,deflate\r\n"
            + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n"
            + "Keep-Alive: 115\r\n"
            + "Connection: keep-alive\r\n"
            + "Content-Length: 12\r\n"
            + "Content-Type: text/plain\r\n"
            + "Cookie: cookie1=testCookie1; cookie2=testCookie2\r\n"
            + "\r\n123456789012\r\n\r\n";

    private RequestParser requestParser;
    private Request request;

    @Before
    public void setUp() throws Exception {
        this.requestParser = new RequestParser();
        request = this.requestParser.parse(new ByteArrayInputStream(REQUEST
                .getBytes()));
    }

    @Test
    public void testParseRequestInfo() throws URISyntaxException {
        assertEquals(Method.POST, request.getMethod());
        assertEquals(new URI("/test/uri/"), request.getRequestUri());
        assertEquals(HTTPVersion.VERSION1_1, request.getVersion());
        assertEquals(new URI("localhost:8079"), request.getHost());
        assertNotNull(request.getHeader());
    }

    @Test
    public void testParseRequestHeader() throws URISyntaxException {
        Header header = request.getHeader();
        assertNotNull(header);
        Set<String> headerFields = header.getHeaderFields();
        assertEquals(7, headerFields.size());
        assertThat(headerFields, hasItems(Header.Fields.ACCEPT,
                Header.Fields.ACCEPT_LANGUAGE, Header.Fields.ACCEPT_ENCODING,
                Header.Fields.ACCEPT_CHARSET, Header.Fields.KEEP_ALIVE,
                Header.Fields.CONNECTION, Header.Fields.HOST));

        List<Header.Parameter> acceptMimetypes = header
                .getHeaderParameter(Header.Fields.ACCEPT);
        assertEquals(4, acceptMimetypes.size());
        assertThat(acceptMimetypes, hasItems(new Header.Parameter("text/html"),
                new Header.Parameter("application/xhtml+xml"), new Header.Parameter(
                "application/xml", 0.9), new Header.Parameter("*/*", 0.8)));

        List<Header.Parameter> acceptLanguage = header
                .getHeaderParameter(Header.Fields.ACCEPT_LANGUAGE);
        assertEquals(2, acceptLanguage.size());
        assertThat(acceptLanguage, hasItems(new Header.Parameter("en-us"),
                new Header.Parameter("en", 0.5)));

        List<Header.Parameter> acceptEncoding = header
                .getHeaderParameter(Header.Fields.ACCEPT_ENCODING);
        assertEquals(2, acceptEncoding.size());
        assertThat(acceptEncoding, hasItems(new Header.Parameter("gzip"),
                new Header.Parameter("deflate")));

        List<Header.Parameter> acceptCharset = header
                .getHeaderParameter(Header.Fields.ACCEPT_CHARSET);
        assertEquals(3, acceptCharset.size());
        assertThat(acceptCharset, hasItems(new Header.Parameter("ISO-8859-1"),
                new Header.Parameter("utf-8", 0.7), new Header.Parameter("*", 0.7)));

        List<Header.Parameter> keepAlive = header
                .getHeaderParameter(Header.Fields.KEEP_ALIVE);
        assertEquals(1, keepAlive.size());
        assertThat(keepAlive, hasItems(new Header.Parameter("115")));

        List<Header.Parameter> connection = header
                .getHeaderParameter(Header.Fields.CONNECTION);
        assertEquals(1, connection.size());
        assertThat(connection, hasItems(new Header.Parameter("keep-alive")));
    }

    @Test
    public void testParseRequestHeaderCookies() throws URISyntaxException {
        Header header = request.getHeader();
        assertNotNull(header);
        List<Cookie> cookies = request.getHeader().getCookies();
        assertEquals(2, cookies.size());
        assertThat(cookies, hasItems(new Cookie("cookie1", "testCookie1"),
                new Cookie("cookie2", "testCookie2")));
    }

    @Test
    public void testParseContent() {
        assertEquals(12, request.getContent().getLength());
        assertEquals("text/plain", request.getContent().getMimetype());
        assertEquals("123456789012", new String(request.getContent()
                .getContent()));
    }

}
