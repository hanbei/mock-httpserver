package de.hanbei.httpserver.common;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 26.02.11
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class Status {

    public static final Status CONTINUE = new Status(100, "Continue");
    public static final Status SWITCHING_PROTOCOLS = new Status(101, "Switching Protocols");
    public static final Status OK = new Status(200, "OK");
    public static final Status CREATED = new Status(201, "Created");
    public static final Status ACCEPTED = new Status(202, "Accepted");
    public static final Status NONAUTHORITATIVE_INFORMATION = new Status(203, "Non-Authoritative Information");
    public static final Status NO_CONTENT = new Status(204, "No Content");
    public static final Status RESET_CONTENT = new Status(205, "Reset Content");
    public static final Status PARTIAL_CONTENT = new Status(206, "Partial Content");
    public static final Status MULTIPLE_CHOICES = new Status(300, "Multiple Choices");
    public static final Status MOVED_PERMANENTLY = new Status(301, "Moved Permanently");
    public static final Status FOUND = new Status(302, "Found");
    public static final Status SEE_OTHER = new Status(303, "See Other");
    public static final Status NOT_MODIFIED = new Status(304, "Not Modified");
    public static final Status USE_PROXY = new Status(305, "Use Proxy");
    public static final Status TEMPORARY_REDIRECT = new Status(307, "Temporary Redirect");
    public static final Status BAD_REQUEST = new Status(400, "Bad Request");
    public static final Status UNAUTHORIZED = new Status(401, "Unauthorized");
    public static final Status PAYMENT_REQUIRED = new Status(402, "Payment Required");
    public static final Status FORBIDDEN = new Status(403, "Forbidden");
    public static final Status NOT_FOUND = new Status(404, "Not Found");
    public static final Status METHOD_NOT_ALLOWED = new Status(405, "Method Not Allowed");
    public static final Status NOT_ACCEPTABLE = new Status(406, "Not Acceptable");
    public static final Status PROXY_AUTHENTICATION_REQUIRED = new Status(407, "Proxy Authentication Required");
    public static final Status REQUEST_TIME_OUT = new Status(408, "Request Time-out");
    public static final Status CONFLICT = new Status(409, "Conflict");
    public static final Status GONE = new Status(410, "Gone");
    public static final Status LENGTH_REQUIRED = new Status(411, "Length Required");
    public static final Status PRECONDITION_FAILED = new Status(412, "Precondition Failed");
    public static final Status REQUEST_ENTITY_TOO_LARGE = new Status(413, "Request Entity Too Large");
    public static final Status REQUEST_URI_TOO_LARGE = new Status(414, "Request-URI Too Large");
    public static final Status UNSUPPORTED_MEDIA_TYPE = new Status(415, "Unsupported Media Type");
    public static final Status REQUESTED_RANGE_NOT_SATISFIABLE = new Status(416, "Requested range not satisfiable");
    public static final Status EXPECTATION_FAILED = new Status(417, "Expectation Failed");
    public static final Status INTERNAL_SERVER_ERROR = new Status(500, "Internal Server Error");
    public static final Status NOT_IMPLEMENTED = new Status(501, "Not Implemented");
    public static final Status BAD_GATEWAY = new Status(502, "Bad Gateway");
    public static final Status SERVICE_UNAVAILABLE = new Status(503, "Service Unavailable");
    public static final Status GATEWAY_TIME_OUT = new Status(504, "Gateway Time-out");
    public static final Status HTTP_VERSION_NOT_SUPPORTED = new Status(505, "HTTP Version not supported");

    private int statusCode;
    private String reasonPhrase;

    public Status(int code) {
        this(code, "");
    }

    public Status(int code, String reason) {
        statusCode = code;
        reasonPhrase = reason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

	public String getReason() {
		return reasonPhrase;
	}

	@Override
	public String toString() {
		return statusCode + " " + reasonPhrase;
	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;

        Status status = (Status) o;

        if (statusCode != status.statusCode) return false;
        if (reasonPhrase != null ? !reasonPhrase.equals(status.reasonPhrase) : status.reasonPhrase != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = statusCode;
        result = 31 * result + (reasonPhrase != null ? reasonPhrase.hashCode() : 0);
        return result;
    }
}
