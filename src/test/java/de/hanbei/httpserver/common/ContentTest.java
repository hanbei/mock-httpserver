package de.hanbei.httpserver.common;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class ContentTest {

	private Content content;

	@Before
	public void setup() {
		content = new Content();
		content.setContent("TestData".getBytes());
		content.setLength(8);
		content.setEncoding("gzip");
		content.setLocation(URI.create("http://localhost/test.html"));
		content.setMimetype("text/plain");
	}

	@Test
	public void testToString() {
		assertEquals("Content-Encoding: gzip\n" + "Content-Type: text/plain\n"
				+ "Content-Length: 8\n\nTestData\n", content.toString());
	}

}
