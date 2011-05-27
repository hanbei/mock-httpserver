package de.hanbei.httpserver.common;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** @author fschulz */
public class MultiValuedMap<K, V> extends HashMap<K, List<V>> {

	private static final long serialVersionUID = 2661811732610654118L;

	public MultiValuedMap() {
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

	public final <A> List<A> get(String key, Class<A> type) {
		Constructor<A> c = null;
		try {
			c = type.getConstructor(String.class);
		} catch (Exception ex) {
			throw new IllegalArgumentException(type.getName()
					+ " has no String constructor", ex);
		}

		ArrayList<A> l = null;
		List<V> values = get(key);
		if (values != null) {
			l = new ArrayList<A>();
			for (V value : values) {
				try {
					l.add(c.newInstance(value));
				} catch (Exception ex) {
					l.add(null);
				}
			}
		}
		return l;
	}

	private List<V> getList(K key) {
		List<V> l = get(key);
		if (l == null) {
			l = new LinkedList<V>();
			put(key, l);
		}
		return l;
	}

	public final <A> A getFirst(K key, Class<A> type) {
		V value = getFirst(key);
		if (value == null) {
			return null;
		}
		Constructor<A> c = null;
		try {
			c = type.getConstructor(String.class);
		} catch (Exception ex) {
			throw new IllegalArgumentException(type.getName()
					+ " has no String constructor", ex);
		}
		A retVal = null;
		try {
			retVal = c.newInstance(value);
		} catch (Exception ex) {
		}
		return retVal;
	}

	@SuppressWarnings("unchecked")
	public final <A> A getFirst(K key, A defaultValue) {
		V value = getFirst(key);
		if (value == null) {
			return defaultValue;
		}

		Class<A> type = (Class<A>) defaultValue.getClass();

		Constructor<A> c = null;
		try {
			c = type.getConstructor(String.class);
		} catch (Exception ex) {
			throw new IllegalArgumentException(type.getName()
					+ " has no String constructor", ex);
		}
		A retVal = defaultValue;
		try {
			retVal = c.newInstance(value);
		} catch (Exception ex) {
		}
		return retVal;
	}

}
