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
package de.hanbei.httpserver.common;

import java.util.*;

import de.hanbei.httpserver.request.Parameter;

public class Header {

	private List<Cookie> cookies;

	private MultiValuedMap<String, Parameter> fields;

	public Header() {
		this.cookies = new ArrayList<Cookie>();
		fields = new MultiValuedMap<String, Parameter>();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (!fields.isEmpty()) {
			for (String parameterKey : fields.keySet()) {
				buffer.append(parameterKey);
				buffer.append(": ");
				List<Parameter> parameterList = fields.get(parameterKey);
				String delim = "";
				for (Parameter p : parameterList) {
					buffer.append(delim).append(p);
					delim = ",";
				}
				buffer.append("\n");
			}
		}
		if (!cookies.isEmpty()) {
			buffer.append(Constants.COOKIE);
			buffer.append(": ");
			int counter = 0;
			for (Cookie cookie : cookies) {
				buffer.append(cookie.getName());
				buffer.append("=");
				buffer.append(cookie.getValue());
				if (counter < cookies.size() - 1) {
					buffer.append("; ");
				}
				counter++;
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	public Set<String> getHeaderFields() {
		return Collections.unmodifiableSet(fields.keySet());
	}

	public List<Parameter> getHeaderParameter(String field) {
		return new ArrayList<Parameter>(fields.get(field));
	}

	public List<String> getHeaderValues(String field) {
		List<Parameter> list = fields.get(field);
		List<String> values = new ArrayList<String>();
		for (Parameter p : list) {
			values.add(p.getValue());
		}
		return values;
	}

	public void addParameter(String parameter, String value) {
		this.fields.add(parameter, new Parameter(value));
	}

	public void addParameter(String fieldName, Parameter parameter) {
		fields.add(fieldName, parameter);
	}

	public List<Cookie> getCookies() {
		return Collections.unmodifiableList(cookies);
	}

	public void setCookies(List<Cookie> cookies) {
		cookies = new ArrayList<Cookie>(cookies);
	}

	public void addParameter(String field, String value, double quality) {
		this.fields.add(field, new Parameter(value, quality));
	}

}
