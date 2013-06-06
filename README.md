mock-httpserver
===============

A Mock Http Server where you can define the answers received on requests optionally based on predicates the request has to fulfill. Its main purpose is to be used in JUnit-Tests.

Starting and stopping the mock-httpserver
-------------------------

Assuming we start the mock-httpserver in the `setUp` method and stop it in the `tearDown` method the code would be:

```java
@Before
public void setUp() {
  mockHttpServer = new MockHttpServer(8888);
  mockHttpServer.start();
}
```  
This starts a mock-httpserver on port 8888. To shut it down call the `stop` method of the mock-httpserver.

```java
@After
public void tearDown() {
  mockHttpServer.stop();
}
```

How to define responses
-----------------------

By default the mock httpserver just returns a 404 Not Found status code on any request. To define another response that should be returned on a request you can use the `addResponse` method of the class `MockHttpServer`. This method takes the HTTP-Method, a relative URI the response should answer to and the Response that should be sent. A response can be built using the builder pattern and the `ResponseBuilder` class. The `Response` class provides some static methods to create a `ResponseBuilder`. 

For example to create a response that returns a HTTP status code of 200 and some json string as content use the `ResponseBuilder` as follows:

```java
Response response = Response.ok().type("application/json").content("{\"some\":\"json\}").build();
```

To tell the mock httpserver to return this response one a GET request to `http://localhost:8888/some/json` do it like this.

```java
mockHttpServer.addResponse(Method.GET, URI.create("/some/json"), response);
```

For details how to construct a response see the javadoc of the class `ResponseBuilder`.


How to process requests
-----------------------

If you need to make sure that a request contains a specific header, query parameter or any other request property you can also process the request sent to the server. For this you have to implement `process` method of the `RequestProcessor` interface and tell the server to process the request sent to a uri. As for normal responses you can bind request processors to a method and a uri as below.

```java
mockHttpServer.addRequestProcessor(Method.POST, URI.create("some/post"), new RequestProcessor() {
  @Override
  public Response process(Request request) {
    List<String> headers = request.getHeader().getHeaderValues(Header.Fields.USER_AGENT);
    if(headers.contains("Mozilla...")) {
      Response.ok().build();
    } else {
      Response.status(Status.UNAUTHORIZED).build();
    }
  }
});
```

Be careful to always send a response back. If you need to assert certain request parameters please do it outside of the `process` method. Otherwise the client will wait for a response that will not be sent.

```java
...
final List<String> headerValues;

mockHttpServer.addRequestProcessor(Method.GET, URI.create("some/post"), new RequestProcessor() {
  @Override
  public Response process(Request request) {
    headerValues = List<String> headers = request.getHeader().getHeaderValues(Header.Fields.USER_AGENT);
    Response.ok().build();
  }
});

assertTrue(headerValues.contains("Mozilla..."));
```
