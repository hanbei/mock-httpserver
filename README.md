mock-httpserver
===============

A Mock Http Server where you can define the answers received on requests optionally based on predicates the request has to fulfill. Its main purpose is to be used in JUnit-Tests.

Starting and stopping the mock-httpserver
-------------------------

Assuming we start the mock-httpserver in the `setUp` method and stop it in the `tearDown` method the code would be:

    @Before
    public void setUp() {
      mockHttpServer = new MockHttpServer(8888);
      mockHttpServer.start();
    }
  
This starts a mock-httpserver on port 8888. To shut it down call the `stop` method of the mock-httpserver.

    @After
    public void tearDown() {
      mockHttpServer.stop();
    }


How to define responses
-----------------------

How to process requests
-----------------------
