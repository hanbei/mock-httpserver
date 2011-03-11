package de.hanbei.httpserver.handler;

import de.hanbei.httpserver.common.Method;
import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 11.03.11
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
public class PredefinedResponseHandlerTest {
    private static final String TEST_CONTENT = "TestContent";
    private PredefinedResponseHandler responseHandler;

    @Before
    public void setUp() throws Exception {
        responseHandler = new PredefinedResponseHandler();
        responseHandler.setDefaultResponse(Response.status(Status.NOT_FOUND).build());
        responseHandler.addResponse(new URI("test/test.html"), Response.ok().content(TEST_CONTENT).build());
    }

    @Test
    public void handlesDefaultRequest() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setRequestUri(new URI("test/wronguri.html"));
        Response response = responseHandler.handleRequest(request);
        assertEquals(Status.NOT_FOUND, response.getStatus());
    }

    @Test
    public void handlesPredefinedRequest() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setRequestUri(new URI("test/test.html"));
        Response response = responseHandler.handleRequest(request);
        assertEquals(Status.OK, response.getStatus());
        assertEquals(TEST_CONTENT, new String(response.getContent().getContent()));
    }
}
