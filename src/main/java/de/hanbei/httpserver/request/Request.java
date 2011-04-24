package de.hanbei.httpserver.request;

import de.hanbei.httpserver.common.Content;
import de.hanbei.httpserver.common.HTTPVersion;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Method;

import java.net.URI;

public class Request {

    private Method method;

    private URI requestUri;

    private HTTPVersion version;

    private URI host;

    private Header header;

    private Content content;

    public Request() {
        header = new Header();
        content = new Content();
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public URI getRequestUri() {
        return this.requestUri;
    }

    public void setRequestUri(URI requestUri) {
        this.requestUri = requestUri;
    }

    public void setVersion(HTTPVersion httpVersion) {
        this.version = httpVersion;
    }

    public HTTPVersion getVersion() {
        return this.version;
    }

    public URI getHost() {
        return this.host;
    }

    public void setHost(URI host) {
        this.host = host;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.method);
        buffer.append(" ");
        buffer.append(this.requestUri);
        buffer.append(" HTTP/");
        buffer.append(this.version);
        buffer.append("\n");
        buffer.append(header.toString());
        return buffer.toString();
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public boolean isEmpty() {
        return (method == null) && (version == null) && (requestUri == null);
    }

}
