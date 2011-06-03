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
package de.hanbei.httpserver.request;

import de.hanbei.httpserver.common.Content;
import de.hanbei.httpserver.common.HTTPVersion;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Method;

import java.net.URI;

import static de.hanbei.httpserver.common.Header.Fields;

public final class Request {

    private Method method;

    private URI requestUri;

    private HTTPVersion version;

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

    /**
     * is the request empty i.e. method, http version and request uri are null. (Used for internal purpose).
     *
     * @return true if method, http version and request uri are null.
     */
    public boolean isEmpty() {
        return (method == null) && (version == null) && (requestUri == null);
    }

    public URI getHost() {
        return URI.create(header.getHeaderValues(Fields.HOST).get(0));
    }
}
