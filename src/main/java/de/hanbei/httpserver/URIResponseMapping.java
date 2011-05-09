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
package de.hanbei.httpserver;

import de.hanbei.httpserver.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps the predefined responses to a uri relative to the server root.
 */
class URIResponseMapping {

    private Map<String, Response> responses;

    public URIResponseMapping() {
        responses = new HashMap<String, Response>();
    }

    /**
     * Get the predefined response for a uri.
     *
     * @param requestUri An uri that should map to a predefined response.
     * @return A predefined response for the requestUri or <code>null</code> if no predefined response exists.
     */
    public Response getResponse(String requestUri) {
        return responses.get(requestUri);
    }

    /**
     * Add a predefined response that should be returned on a specific request uri.
     * @param uri The uri the predefined response should be map to.
     * @param response The predefined response.
     */
    public void addResponse(String uri, Response response) {
        responses.put(uri, response);
    }
}
