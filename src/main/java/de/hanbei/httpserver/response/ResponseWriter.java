package de.hanbei.httpserver.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.Charsets;

import de.hanbei.httpserver.common.Content;
import de.hanbei.httpserver.common.Cookie;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.request.Request;

/**
 *
 *
 */
public class ResponseWriter {

    private final OutputStream out;
    private final Request request;

    public ResponseWriter(OutputStream outputStream, Request request) {
        this.out = outputStream;
        this.request = request;
    }

    public void write(Response response) throws IOException {
        List<Header.Parameter> acceptCharsetList = request.getHeader().getHeaderParameter(Header.Fields.ACCEPT_CHARSET);
        Charset acceptCharset = Charsets.UTF_8;
        if ( !acceptCharsetList.isEmpty() ) {
            acceptCharset = Charset.forName(acceptCharsetList.get(0).getValue());
        }

        OutputStreamWriter charOut = new OutputStreamWriter(this.out, acceptCharset);
        writeHttpVersion(response, charOut);
        writeStatus(response, charOut);
        writeHeader(response.getHeader(), charOut);
        writeContent(response.getContent(), charOut);
        charOut.write("\n");
        charOut.close();
    }

    private void writeHttpVersion(Response response, OutputStreamWriter charOut) throws IOException {
        charOut.write("HTTP/");
        charOut.write(response.getHttpVersion().toString());
        charOut.write(" ");
    }

    private void writeStatus(Response response, OutputStreamWriter charOut) throws IOException {
        charOut.write(response.getStatus().toString());
        charOut.write("\n");
    }

    private void writeHeader(Header header, OutputStreamWriter charOut) throws IOException {
        if ( !header.getHeaderFields().isEmpty() ) {
            for ( String parameterKey : header.getHeaderFields() ) {
                charOut.write(parameterKey);
                charOut.write(": ");
                List<Header.Parameter> parameterList = header.getHeaderParameter(parameterKey);
                String delimiter = "";
                for ( Header.Parameter p : parameterList ) {
                    charOut.write(delimiter);
                    charOut.write(p.toString());
                    delimiter = ",";
                }
                charOut.write("\n");
            }
        }
        if ( !header.getCookies().isEmpty() ) {
            charOut.write(Header.Fields.COOKIE);
            charOut.write(": ");
            String delim = "";
            for ( Cookie cookie : header.getCookies() ) {
                charOut.write(delim);
                charOut.write(cookie.getName());
                charOut.write("=");
                charOut.write(cookie.getValue());
                delim = "; ";
            }
        }
    }

    private void writeContent(Content content, OutputStreamWriter charOut) throws IOException {
        if ( content != null ) {
            if ( content.getEncoding() != null ) {
                charOut.write(Header.Fields.CONTENT_ENCODING);
                charOut.write(": ");
                charOut.write(content.getEncoding());
                charOut.write("\n");
            }
            if ( content.getMimetype() != null ) {
                charOut.write(Header.Fields.CONTENT_TYPE);
                charOut.write(": ");
                charOut.write(content.getMimetype());
                if ( content.getCharset() != null ) {
                    charOut.write("; charset=");
                    charOut.write(content.getCharset());
                }
                charOut.write("\n");
            }
            if ( content.getLength() > 0 ) {
                charOut.write(Header.Fields.CONTENT_LENGTH);
                charOut.write(": ");
                charOut.write(Integer.toString(content.getLength()));
                charOut.write("\n");
            }
            if ( content.getLanguage() != null ) {
                charOut.write(Header.Fields.CONTENT_LANGUAGE);
                charOut.write(": ");
                charOut.write(content.getLanguage());
                charOut.write("\n");
            }
            if ( content.getMd5() != null ) {
                charOut.write(Header.Fields.CONTENT_MD5);
                charOut.write(": ");
                charOut.write(content.getMd5());
                charOut.write("\n");
            }
            if ( content.getRange() != null ) {
                charOut.write(Header.Fields.CONTENT_RANGE);
                charOut.write(": ");
                charOut.write(content.getRange());
                charOut.write("\n");
            }
            charOut.write("\n");
            charOut.flush();
            if ( content.isString() ) {
                charOut.write(new String(content.getContent(), content.getCharset()));
            } else {
                out.write(content.getContent());
                out.flush();
            }
        }
        charOut.write("\n");
        charOut.flush();
    }

    public void close() throws IOException {
        out.flush();
        out.close();
    }
}
