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
package de.hanbei.httpserver.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author fschulz
 */
public class MultiValuedMap<K, V> extends HashMap<K, List<V>> {

    private static final long serialVersionUID = 2661811732610654118L;

    public MultiValuedMap() {
        super();
    }

    public MultiValuedMap(MultiValuedMap<K, V> that) {
        for (Map.Entry<K, List<V>> e : that.entrySet()) {
            this.put(e.getKey(), new ArrayList<V>(e.getValue()));
        }
    }

    public final void putSingle(K key, V value) {
        List<V> l = getList(key);

        l.clear();
        if (value != null) {
            l.add(value);
        }
    }

    public final void add(K key, V value) {
        List<V> l = getList(key);

        if (value != null) {
            l.add(value);
        }
    }

    public final V getFirst(K key) {
        List<V> values = get(key);
        if (values != null && values.size() > 0) {
            return values.get(0);
        } else {
            return null;
        }
    }

    public final void addFirst(K key, V value) {
        List<V> l = getList(key);

        if (value != null) {
            l.add(0, value);
        }
    }


    private List<V> getList(K key) {
        List<V> l = get(key);
        if (l == null) {
            l = new LinkedList<V>();
            put(key, l);
        }
        return l;
    }


}
