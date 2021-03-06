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

import java.net.URI;

/**
 * A class to save the content send with a request or a response. Holds all necessary information of content i.e. content length, mimetype,
 * encoding and the actual content as byte array.
 */
public class Content {

    private static final String UTF8_ENCODING = "utf-8";
    private String encoding;

    private String mimetype;

    private String charset = UTF8_ENCODING;

    private int length = -1;

    private byte[] content;

    private URI location;

    private String language;

    private String md5;

    private String range;
    private boolean string;

    public String getComposedContentType() {
        if ( mimetype != null ) {
            if ( charset == null ) {
                return mimetype;
            } else {
                return mimetype + "; charset=" + charset;
            }
        }
        return null;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = getType(mimetype);
        this.charset = getCharset(mimetype);
    }

    private String getType(String mimetype) {
        return mimetype.split(";")[0];
    }

    private String getCharset(String mimetype) {
        String[] charset = mimetype.split(";");
        if ( charset.length > 1 ) {
            return charset[1].split("=")[1];
        }
        return UTF8_ENCODING;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        if ( length < 0 ) {
            return new byte[0];
        }
        byte[] result = new byte[content.length];
        System.arraycopy(content, 0, result, 0, content.length);
        return result;
    }

    public void setContent(byte[] content) {
        this.content = new byte[content.length];
        System.arraycopy(content, 0, this.content, 0, content.length);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public URI getLocation() {
        return location;
    }

    public void setLocation(URI location) {
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if ( content != null ) {
            if ( encoding != null ) {
                builder.append(Header.Fields.CONTENT_ENCODING);
                builder.append(": ");
                builder.append(encoding);
                builder.append("\n");
            }
            if ( mimetype != null ) {
                builder.append(Header.Fields.CONTENT_TYPE);
                builder.append(": ");
                builder.append(mimetype);
                if ( charset != null ) {
                    builder.append("; charset=");
                    builder.append(charset);
                }
                builder.append("\n");
            }
            if ( length > 0 ) {
                builder.append(Header.Fields.CONTENT_LENGTH);
                builder.append(": ");
                builder.append(length);
                builder.append("\n");
            }
            if ( language != null ) {
                builder.append(Header.Fields.CONTENT_LANGUAGE);
                builder.append(": ");
                builder.append(language);
                builder.append("\n");
            }
            if ( md5 != null ) {
                builder.append(Header.Fields.CONTENT_MD5);
                builder.append(": ");
                builder.append(md5);
                builder.append("\n");
            }
            if ( range != null ) {
                builder.append(Header.Fields.CONTENT_RANGE);
                builder.append(": ");
                builder.append(range);
                builder.append("\n");
            }
            builder.append("\n");
            builder.append(new String(content));
        }
        builder.append("\n");
        return builder.toString();
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return md5;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getRange() {
        return range;
    }

    public String getContentAsString() {
        if ( content != null ) {
            return new String(content);
        }
        return "";
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setString(boolean string) {
        this.string = string;
    }

    public boolean isString() {
        return string;
    }
}
