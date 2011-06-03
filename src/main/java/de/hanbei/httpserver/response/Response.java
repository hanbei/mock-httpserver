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

package de.hanbei.httpserver.response;

import de.hanbei.httpserver.common.Content;
import de.hanbei.httpserver.common.HTTPVersion;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Status;

/**
 * A HTTP response. Instances of this class can be build by calling the static creation methods and using the {@link
 * ResponseBuilder} class.
 */
public final class Response {

    private HTTPVersion httpVersion = HTTPVersion.VERSION1_1;

    private Status status;

    private Header header;

    private Content content;

    private Response() {
    }

    private Response(Status status) {
        this.status = status;
        header = new Header();
        content = new Content();
    }

    /**
     * Get the status code of this response.
     *
     * @return The status code of this response.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status code of this response.
     *
     * @param status The status code of this response.
     */
    void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the used HTTP version for this response. Defaults to 1.1
     *
     * @return The used HTTP version.
     */
    public HTTPVersion getHttpVersion() {
        return httpVersion;
    }

    /**
     * Set the HTTP Version of this response.
     *
     * @param httpVersion The version of the HTTP Protocol.
     */
    void setHttpVersion(HTTPVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/");
        builder.append(httpVersion);
        builder.append(" ");
        builder.append(status);
        builder.append("\n");
        builder.append(header);
        builder.append(content);
        return builder.toString();
    }

    /**
     * Get the header fields of this response and set them.
     *
     * @return The header fields of this response.
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Get the content that will be sent with this response.
     *
     * @return The content of this response.
     */
    public Content getContent() {
        return content;
    }

    /**
     * Create a response that represents a 404 Not Found. Additional information can be set via the returned {@link
     * ResponseBuilder} class.
     *
     * @return A ResponseBuilder with a base response initialized on 404 - Not Found.
     */
    public static ResponseBuilder notFound() {
        return status(Status.NOT_FOUND);
    }

    /**
     * Create a response that represents a 200 - Ok. Additional information can be set via the returned {@link
     * ResponseBuilder} class.
     *
     * @return A ResponseBuilder with a base response initialized on 200 - Ok.
     */
    public static ResponseBuilder ok() {
        return status(Status.OK);
    }


    /**
     * Create a response that represents some status. Additional information can be set via the returned {@link
     * ResponseBuilder} class.
     *
     * @return A ResponseBuilder with a base response initialized on <code>status</code>.
     */
    public static ResponseBuilder status(Status status) {
        return new ResponseBuilder(new Response(status));
    }
}
