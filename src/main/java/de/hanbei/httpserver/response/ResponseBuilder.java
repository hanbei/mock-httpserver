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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.hanbei.httpserver.common.HTTPVersion;
import de.hanbei.httpserver.common.Header;
import de.hanbei.httpserver.common.Status;
import de.hanbei.httpserver.exceptions.ContentException;

/** Builder Pattern implementation for building responses. */
public class ResponseBuilder {

	private Response response;

	private final SimpleDateFormat dateFormat;

	ResponseBuilder(Response response) {
		this.response = response;
		dateFormat = new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Actually build the response.
	 * 
	 * @return The build response.
	 */
	public Response build() {
		return response;
	}

	/**
	 * Set the status of the response.
	 * 
	 * @param status
	 *            The status as as an integer.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder status(int status) {
		response.setStatus(new Status(status));
		return this;
	}

	/**
	 * Set the status of the response.
	 * 
	 * @param status
	 *            The status to set.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder status(Status status) {
		response.setStatus(status);
		return this;
	}

	/**
	 * Set the http version of the response.
	 * 
	 * @param version
	 *            A HTTPVersion.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder version(HTTPVersion version) {
		response.setHttpVersion(version);
		return this;
	}

	/**
	 * Set the content of the response as any object. The content length is
	 * induced from the serialized object.
	 * 
	 * @param content
	 *            The content as as an object.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder content(Object content) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream(bytes);
			objectOut.writeObject(content);
		} catch (IOException e) {
			throw new ContentException(e);
		}
		content(bytes.toByteArray());
		return this;
	}

	/**
	 * Set the content as a String. The content length is induced from the
	 * string length
	 * 
	 * @param content
	 *            The content as as a string.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder content(String content) {
		content(content.getBytes());
		return this;
	}

	/**
	 * Set the content as a byte array. The content length is induced from the
	 * array length.
	 * 
	 * @param content
	 *            The content as as a byte array.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder content(byte[] content) {
		response.getContent().setContent(content);
		response.getContent().setLength(content.length);
		return this;
	}

	public ResponseBuilder expires(Date expires) {
		response.getHeader().addParameter(Header.Fields.EXPIRES,
				dateFormat.format(expires));
		return this;
	}

	/**
	 * Add a header field to the response.
	 * 
	 * @param name
	 *            The name of the header field.
	 * @param value
	 *            The value of the header field.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder header(String name, String value) {
		response.getHeader().addParameter(name, value);
		return this;
	}

	/**
	 * Add a language to the response.
	 * 
	 * @param language
	 *            The language as ISO-??? string.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder language(String language) {
		response.getHeader().addParameter("Accept-Language", language);
		return this;
	}

	/**
	 * Set the last modified header field of the response.
	 * 
	 * @param lastModified
	 *            The date the reponse was last modified.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder lastModified(Date lastModified) {
		response.getHeader().addParameter(Header.Fields.LAST_MODIFIED,
				dateFormat.format(lastModified));
		return this;
	}

	/**
	 * @param location
	 *            The location of the content.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder location(URI location) {
		response.getContent().setLocation(location);
		return this;
	}

	/**
	 * Set the response mimetype header field.
	 * 
	 * @param type
	 *            The type of the response.
	 * @return A ResponseBuilder to add additional information.
	 */
	public ResponseBuilder type(String type) {
		response.getHeader().addParameter("Accept", type);
		response.getContent().setMimetype(type);
		return this;
	}

}
