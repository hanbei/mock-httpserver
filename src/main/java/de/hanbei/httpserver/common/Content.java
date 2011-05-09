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
 * A class to save the content send with a request or a response. Holds all necessary information of content i.e.
 * content length, mimetype, encoding and the actual content as byte array.
 */
public class Content {

    private String encoding;

    private String mimetype;

    private int length = -1;

    private byte[] content;

    private URI location;

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
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
        if (encoding != null) {
            builder.append(Constants.CONTENT_ENCODING);
            builder.append(": ");
            builder.append(encoding);
            builder.append("\n");
        }
        if (mimetype != null) {
            builder.append(Constants.CONTENT_TYPE);
            builder.append(": ");
            builder.append(mimetype);
            builder.append("\n");
        }
        if (length > 0) {
            builder.append(Constants.CONTENT_LENGTH);
            builder.append(": ");
            builder.append(length);
            builder.append("\n");
        }
        builder.append("\n");
        if (content != null) {
            builder.append(new String(content));
            builder.append("\n");
        }
        return builder.toString();
    }
}
