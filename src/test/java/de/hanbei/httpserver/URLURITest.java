package de.hanbei.httpserver;

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
