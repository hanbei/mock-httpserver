package de.hanbei.httpserver.common;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 27.02.11
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
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
