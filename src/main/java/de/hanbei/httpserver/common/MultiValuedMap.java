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
