package de.hanbei.httpserver.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.hanbei.httpserver.common.Constants;
import de.hanbei.httpserver.common.Status;

/**
 * Created by IntelliJ IDEA. User: hanbei Date: 26.02.11 Time: 13:01 To change
 * this template use File | Settings | File Templates.
 */
public class ResponseBuilder {

	private Response response;

	private SimpleDateFormat dateFormat;

	ResponseBuilder(Response response) {
		this.response = response;
		dateFormat = new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public Response build() {
		return response;
	}

	public ResponseBuilder status(int status) {
		response.setStatus(new Status(status));
		return this;
	}

	public ResponseBuilder status(Status status) {
		response.setStatus(status);
		return this;
	}

	public ResponseBuilder version(String version) {
		response.setHttpVersion(version);
		return this;
	}

	public ResponseBuilder content(Object content) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream(bytes);
			objectOut.writeObject(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		content(bytes.toByteArray());
		return this;
	}

	public ResponseBuilder content(String content) {
		content(content.getBytes());
		return this;
	}

	public ResponseBuilder content(byte[] content) {
		response.getContent().setContent(content);
		response.getContent().setLength(content.length);
		return this;
	}

	public ResponseBuilder expires(Date expires) {
		response.getHeader().addParameter(Constants.EXPIRES,
				dateFormat.format(expires));
		return this;
	}

	public ResponseBuilder header(String name, String value) {
		response.getHeader().addParameter(name, value);
		return this;
	}

	public ResponseBuilder language(String language) {
		response.getHeader().addAcceptLanguage(language);
		return this;
	}

	public ResponseBuilder lastModified(Date lastModified) {
		response.getHeader().addParameter(Constants.LAST_MODIFIED,
				dateFormat.format(lastModified));
		return this;
	}

	public ResponseBuilder location(URI location) {
		response.getContent().setLocation(location);
		return this;
	}

	public ResponseBuilder type(String type) {
		response.getHeader().addAcceptMimetype(type);
		response.getContent().setMimetype(type);
		return this;
	}

}
