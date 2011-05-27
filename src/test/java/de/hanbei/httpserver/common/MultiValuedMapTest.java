package de.hanbei.httpserver.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.hasItems;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MultiValuedMapTest {

	private MultiValuedMap<String, String> map;

	@Before
	public void setup() {
		map = new MultiValuedMap<String, String>();
	}

	@Test
	public void testAdd() {
		map.add("Test", "Test1");
		map.add("Test", "Test2");
		map.add("Test", "Test3");

		List<String> list = map.get("Test");
		assertEquals(3, list.size());
		assertThat(list, hasItems("Test1", "Test2", "Test3"));
	}

	@Test
	public void testGetFirst() {
		map.add("Test", "Test2");
		map.add("Test", "Test1");
		map.add("Test", "Test3");

		assertEquals("Test2", map.getFirst("Test"));
	}

	@Test
	public void testAddFirst() {
		map.add("Test", "Test2");
		map.add("Test", "Test1");
		map.add("Test", "Test3");
		assertEquals("Test2", map.getFirst("Test"));
		map.addFirst("Test", "Test4");
		assertEquals("Test4", map.getFirst("Test"));
	}

	@Test
	public void testPutSingle() {
		map.add("Test", "Test2");
		map.add("Test", "Test1");
		map.add("Test", "Test3");
		List<String> list = map.get("Test");
		assertEquals(3, list.size());
		assertThat(list, hasItems("Test1", "Test2", "Test3"));
		
		map.putSingle("Test", "Test5");
		list = map.get("Test");
		assertEquals(1, list.size());
		assertThat(list, hasItems("Test5"));
	}

}
