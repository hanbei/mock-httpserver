package de.hanbei.httpserver.response;

import de.hanbei.httpserver.common.Content;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Status;

/**
 * Created by IntelliJ IDEA. User: hanbei Date: 26.02.11 Time: 12:39 To change
 * this template use File | Settings | File Templates.
 */
public class Response {

    private String httpVersion = "1.1";

    private Status status;

    private Header header;

    private Content content;

    public Response() {
    }

    public Response(Status status) {
        this.status = status;
        header = new Header();
        content = new Content();
    }

    public Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    void setHttpVersion(String httpVersion) {
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

    public static ResponseBuilder ok() {
    	return status(Status.OK);
    }

    public static ResponseBuilder status(Status status) {
        ResponseBuilder builder = new ResponseBuilder(new Response(status));
        return builder;
    }

    public Header getHeader() {
        return header;
    }

    public Content getContent() {
        return content;
    }

	public static ResponseBuilder notFound() {
		return status(Status.NOT_FOUND);
	}
}
