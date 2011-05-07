package de.hanbei.httpserver.request;

import de.hanbei.httpserver.common.Cookie;
import de.hanbei.httpserver.common.HTTPVersion;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Method;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.junit.Assert.*;

public class RequestParserTest {

	private static final String REQUEST = "GET /test/uri/ HTTP/1.1\nHost: localhost:8079\n"
			+ "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n"
			+ "Accept-Language: en-us,en;q=0.5\n"
			+ "Accept-Encoding: gzip,deflate\n"
			+ "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n"
			+ "Keep-Alive: 115\n"
			+ "Connection: keep-alive\n"
			+ "Cookie: cookie1=testCookie1; cookie2=testCookie2\n";

	// private static final String CONTENT = "Content-Type: text/xml charset=";
	private RequestParser requestParser;
	private Request request;

	@Before
	public void setUp() throws Exception {
		this.requestParser = new RequestParser();
		request = this.requestParser.parse(new ByteArrayInputStream(REQUEST
				.getBytes()));
	}

	@Test
	public void testParseRequestInfo() throws URISyntaxException {
		assertEquals(Method.GET, request.getMethod());
		assertEquals(new URI("/test/uri/"), request.getRequestUri());
		assertEquals(HTTPVersion.VERSION1_1, request.getVersion());
		assertEquals(new URI("localhost:8079"), request.getHost());
		assertNotNull(request.getHeader());
	}

	@Test
	public void testParseRequestHeaderAcceptMimetype()
			throws URISyntaxException {
		Header header = request.getHeader();
		assertNotNull(header);
		Set<String> acceptMimetypes = header.getAcceptMimetypes();
		assertThat(acceptMimetypes, hasItems("text/html",
				"application/xhtml+xml", "application/xml", "*/*"));
		assertEquals(1.0, header.getAcceptMimetypeQuality("text/html"),
				0.00000001);
		assertEquals(1.0, header
				.getAcceptMimetypeQuality("application/xhtml+xml"), 0.00000001);
		assertEquals(0.9, header.getAcceptMimetypeQuality("application/xml"),
				0.00000001);
		assertEquals(0.8, header.getAcceptMimetypeQuality("*/*"), 0.00000001);
		assertEquals(-1.0, header.getAcceptMimetypeQuality("text/plain"),
				0.00000001);
	}

	@Test
	public void testParseRequestHeaderAcceptLanguage()
			throws URISyntaxException {
		Header header = request.getHeader();
		assertNotNull(header);
		Set<String> acceptLanguages = header.getAcceptLanguages();
		assertThat(acceptLanguages, hasItems("en-us", "en"));
		assertEquals(1.0, header.getAcceptLanguageQuality("en-us"), 0.00000001);
		assertEquals(0.5, header.getAcceptLanguageQuality("en"), 0.00000001);
	}

	@Test
	public void testParseRequestHeaderAcceptEncoding()
			throws URISyntaxException {
		Header header = request.getHeader();
		assertNotNull(header);
		Set<String> acceptEncoding = header.getAcceptEncoding();
		assertThat(acceptEncoding, hasItems("gzip", "deflate"));
		assertEquals(1.0, header.getAcceptEncodingQuality("gzip"), 0.00000001);
		assertEquals(1.0, header.getAcceptEncodingQuality("deflate"),
				0.00000001);
	}

	@Test
	public void testParseRequestHeaderCookies() throws URISyntaxException {
		Header header = request.getHeader();
		assertNotNull(header);
		List<Cookie> cookies = request.getHeader().getCookies();
		assertEquals(2, cookies.size());
		assertThat(cookies, hasItems(new Cookie("cookie1", "testCookie1"),
				new Cookie("cookie2", "testCookie2")));
	}

	@Test
	public void testParseRequestHeaderParameter() throws URISyntaxException {
		Header header = request.getHeader();
		assertNotNull(header);
		Map<String, String> parameter = request.getHeader().getParameter();
		assertEquals(2, parameter.size());
		assertThat(parameter.keySet(), hasItems("Keep-Alive", "Connection"));
		assertThat(parameter.values(), hasItems("keep-alive", "115"));
	}

	@Test
	public void testParseRequestHeaderAcceptCharset() throws URISyntaxException {
		Request request = this.requestParser.parse(new ByteArrayInputStream(
				REQUEST.getBytes()));
		Header header = request.getHeader();
		assertNotNull(header);
		Set<String> acceptCharset = request.getHeader().getAcceptCharset();
		assertThat(acceptCharset, hasItems("ISO-8859-1", "utf-8", "*"));
		assertEquals(1.0, header.getAcceptCharsetQuality("ISO-8859-1"),
				0.00000001);
		assertEquals(0.7, header.getAcceptCharsetQuality("utf-8"), 0.00000001);
		assertEquals(0.7, header.getAcceptCharsetQuality("*"), 0.00000001);
	}

}
