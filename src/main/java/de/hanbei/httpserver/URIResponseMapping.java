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

import de.hanbei.httpserver.response.Response;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 31.03.11
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
class URIResponseMapping {

    private Map<URI, Response> responses;

    public URIResponseMapping() {
        responses = new HashMap<URI, Response>();
    }

    public Response getResponse(URI requestUri) {
        return responses.get(requestUri);
    }

    public void addResponse(URI uri, Response response) {
        responses.put(uri, response);
    }
}
