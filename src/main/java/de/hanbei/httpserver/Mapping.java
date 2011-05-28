package de.hanbei.httpserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps a predefined Response or RequestProcessor to a uri relative to the server root.
 */
public class Mapping<V> {

    protected Map<String, V> responses;

    public Mapping() {
        responses = new HashMap<String, V>();
    }

    /**
     * Get the predefined response for a uri.
     *
     * @param requestUri An uri that should map to a predefined response.
     * @return A predefined response for the requestUri or <code>null</code> if no predefined response exists.
     */
    public V get(String requestUri) {
        return responses.get(requestUri);
    }

    /**
     * Add a predefined response that should be returned on a specific request uri.
     *
     * @param uri      The uri the predefined response should be map to.
     * @param response The predefined response.
     */
    public void add(String uri, V response) {
        responses.put(uri, response);
    }
}
