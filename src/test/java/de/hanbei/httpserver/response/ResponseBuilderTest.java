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
package de.hanbei.httpserver.response;

import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Status;
import org.junit.Test;

import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/** Created by IntelliJ IDEA. User: hanbei Date: 27.02.11 Time: 12:58 To change this template use File | Settings | File Templates. */
public class ResponseBuilderTest {

    @Test
    public void testOk() throws Exception {
        Response response = Response.ok().build();
        assertEquals(Status.OK, response.getStatus());
    }

    @Test
    public void testStatus() throws Exception {
        Response response = Response.status(Status.NOT_FOUND).build();
        assertEquals(Status.NOT_FOUND, response.getStatus());
    }

    @Test
    public void testLanguage() throws Exception {
        Response response = Response.ok().language("en").build();
        assertThat(response.getHeader().getHeaderValues("Accept-Language"),
                hasItems("en"));
        assertEquals("HTTP/1.1 200 OK\nAccept-Language: en\n\n", response
                .toString());
    }

    @Test
    public void contentIsAddedRight() throws Exception {
        Response response = Response.ok().header(Header.Fields.DATE,
                "2011-12-12").content("TestContent").build();
        assertEquals(11, response.getContent().getLength());
        assertEquals("TestContent", new String(response.getContent()
                .getContent()));
        assertEquals("HTTP/1.1 200 OK\n" + "Date: 2011-12-12\n" + "Content-Length: 11\n" + "\n" + "TestContent\n", response
                .toString());
    }

}
