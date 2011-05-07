package de.hanbei.httpserver.request;

import de.hanbei.httpserver.common.Header;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HeaderTest {

    private Header header;

    @Before
    public void setUp() throws Exception {
        this.header = new Header();
        this.header.addAcceptMimetype("text/xml");
        this.header.addAcceptMimetype("text/html", 0.1);

        this.header.addAcceptLanguage("en", 0.1);
        this.header.addAcceptLanguage("en-us", 0.5);

        this.header.addAcceptEncoding("gzip");
        this.header.addAcceptEncoding("deflate");

        this.header.addAcceptCharset("ISO-8859-1");
        this.header.addAcceptCharset("utf-8", 0.7);
        this.header.addAcceptCharset("*", 0.7);
    }

    @Test
    public void testToString() {
        assertEquals("Accept: text/xml,text/html;q=0.1\n" +
                "Accept-Language: en;q=0.1,en-us;q=0.5\n" +
                "Accept-Encoding: gzip,deflate\n" +
                "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n", this.header.toString());
    }

}

