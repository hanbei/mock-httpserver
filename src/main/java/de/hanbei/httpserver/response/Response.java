package de.hanbei.httpserver.response;

import de.hanbei.httpserver.common.Content;
import de.hanbei.httpserver.common.HTTPVersion;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Status;

/**
 * A HTTP response. Instances of this class can be build by calling the static creation methods and using the
 * {@link ResponseBuilder} class.
 */
public class Response {

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
     * @return The status code of this response.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status code of this response.
     * @param status The status code of this response.
     */
    void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the used HTTP version for this response. Defaults to 1.1
     * @return The used HTTP version.
     */
    public HTTPVersion getHttpVersion() {
        return httpVersion;
    }

    /**
     * Set the HTTP Version of this response.
     * @param httpVersion
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
     * @return The header fields of this response.
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Get the content that will be sent with this response.
     * @return The content of this response.
     */
    public Content getContent() {
        return content;
    }

    public static ResponseBuilder notFound() {
        return status(Status.NOT_FOUND);
    }

    public static ResponseBuilder ok() {
        return status(Status.OK);
    }

    public static ResponseBuilder status(Status status) {
        ResponseBuilder builder = new ResponseBuilder(new Response(status));
        return builder;
    }
}
