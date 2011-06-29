package de.hanbei.httpserver.common;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

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
		assertEquals("Content-Encoding: gzip\n" + "Content-Type: text/plain; charset=utf-8\n"
				+ "Content-Length: 8\n\nTestData\n", content.toString());
	}

    @Test
	public void testSetMimetype() {
        content.setMimetype("text/plain; charset=us-ascii");
		assertEquals("Content-Encoding: gzip\n" + "Content-Type: text/plain; charset=us-ascii\n"
				+ "Content-Length: 8\n\nTestData\n", content.toString());
	}

}
