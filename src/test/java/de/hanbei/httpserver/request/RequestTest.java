package de.hanbei.httpserver.request;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RequestTest {

    private Request request;

    @Before
    public void setUp() throws Exception {
        request = new Request();
        request.setRequestUri(new URI("http://localhost:8080/search?query=Query&param=Param"));
    }

    @Test
    public void testGetQueryParameter() throws Exception {
        List<QueryParameter> queryParameters = request.getQueryParameter();
        assertEquals(2, queryParameters.size());
        assertEquals(new QueryParameter("query", "Query"), queryParameters.get(0));
        assertEquals(new QueryParameter("param", "Param"), queryParameters.get(1));
    }

    @Test
    public void testGetEmptyQueryParameter() throws Exception {
        request.setRequestUri(new URI("http://localhost:8080/search"));

        List<QueryParameter> queryParameters = request.getQueryParameter();
        assertEquals(0, queryParameters.size());
    }

}
