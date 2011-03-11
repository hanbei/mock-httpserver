package de.hanbei.httpserver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

public class URLURITest
{

    @Test(expected = MalformedURLException.class)
    public void testURLConstruction() throws MalformedURLException {
        new URL("/");
    }

    @Test
    public void testURIConstruction() throws URISyntaxException {
        new URI("/");
    }

}
