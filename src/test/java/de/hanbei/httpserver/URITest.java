package de.hanbei.httpserver;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class URITest {

    @Before
    public void setup() {

    }

    @Test
    public void getQueryPart() throws URISyntaxException {
        URI uri = new URI("http://localhost:8080/search?query=Test&param=Param2");
        assertEquals("query=Test&param=Param2", uri.getQuery());
    }
}
