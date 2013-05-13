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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Header {

    public static final class Fields {
        public static final String CONNECTION = "Connection";
        public static final String DATE = "Date";
        public static final String PRAGMA = "Pragma";
        public static final String TRANSFER_ENCODING = "Transfer-Encoding";
        public static final String WARNING = "Warning";

        public static final String AGE = "Age";
        public static final String KEEP_ALIVE = "Keep-Alive";
        public static final String COOKIE = "Cookie";
        public static final String ACCEPT_MIMETYPE = "Accept";
        public static final String ACCEPT = "Accept";
        public static final String ACCEPT_LANGUAGE = "Accept-Language";
        public static final String ACCEPT_ENCODING = "Accept-Encoding";
        public static final String ACCEPT_CHARSET = "Accept-Charset";
        public static final String ACCEPT_RANGES = "Accept-Ranges";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_LENGTH = "Content-Length";
        public static final String CONTENT_ENCODING = "Content-Encoding";
        public static final String CONTENT_LANGUAGE = "Content-Language";
        public static final String CONTENT_LOCATION = "Content-Location";
        public static final String CONTENT_RANGE = "Content-Range";
        public static final String CONTENT_MD5 = "Content-MD5";
        public static final String ETAG = "ETag";
        public static final String EXPECT = "Expect";
        public static final String EXPIRES = "Expires";
        public static final String HOST = "Host";
        public static final String LAST_MODIFIED = "Last-Modified";
        public static final String LOCATION = "Location";
        public static final String USER_AGENT = "User-Agent";
        public static final String ALLOW = "Allow";

        private Fields() {
            // no construction
        }
    }

    public static class Parameter {

        private static final double EPSILON = 0.00000000000001;
        private String value;
        private double quality;

        public Parameter(String value) {
            this(value, -1);
        }

        public Parameter(String value, double quality) {
            this.value = value;
            this.quality = quality;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(quality);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) {
                return true;
            }
            if ( obj == null ) {
                return false;
            }
            if ( getClass() != obj.getClass() ) {
                return false;
            }
            Parameter other = (Parameter) obj;
            if ( Double.doubleToLongBits(quality) != Double
                .doubleToLongBits(other.quality) ) {
                return false;
            }
            if ( value == null ) {
                if ( other.value != null ) {
                    return false;
                }
            } else if ( !value.equals(other.value) ) {
                return false;
            }
            return true;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public double getQuality() {
            return quality;
        }

        public void setQuality(double quality) {
            this.quality = quality;
        }

        @Override
        public String toString() {
            if ( Math.abs(quality + 1) < EPSILON ) {
                return value;
            }
            return value + ";q=" + quality;
        }
    }

    private List<Cookie> cookies;

    private MultiValuedMap<String, Parameter> fields;

    public Header() {
        this.cookies = new ArrayList<Cookie>();
        fields = new MultiValuedMap<String, Parameter>();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if ( !fields.isEmpty() ) {
            for ( String parameterKey : fields.keySet() ) {
                buffer.append(parameterKey);
                buffer.append(": ");
                List<Parameter> parameterList = fields.get(parameterKey);
                String delim = "";
                for ( Parameter p : parameterList ) {
                    buffer.append(delim).append(p);
                    delim = ",";
                }
                buffer.append("\n");
            }
        }
        if ( !cookies.isEmpty() ) {
            buffer.append(Header.Fields.COOKIE);
            buffer.append(": ");
            String delim = "";
            for ( Cookie cookie : cookies ) {
                buffer.append(delim).append(cookie.getName()).append("=")
                      .append(cookie.getValue());
                delim = "; ";
            }
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
        List<Parameter> parameters = fields.get(field);
        if ( parameters == null ) {
            return Collections.emptyList();
        }
        return new ArrayList<Parameter>(parameters);
    }

    public List<String> getHeaderValues(String field) {
        List<Parameter> list = fields.get(field);
        List<String> values = new ArrayList<String>();
        for ( Parameter p : list ) {
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
        this.cookies = new ArrayList<Cookie>(cookies);
    }

    public void addParameter(String field, String value, double quality) {
        this.fields.add(field, new Parameter(value, quality));
    }

}
