package de.hanbei.httpserver.request;

import de.hanbei.httpserver.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

public class RequestParser {


    private static final Logger LOGGER = LoggerFactory.getLogger(RequestParser.class);

    private static class QualityParameter {
        public String value;
        public double quality;

        public QualityParameter(String value, double quality) {
            this.value = value;
            this.quality = quality;
        }
    }

    public Request parse(InputStream in) {
        //BufferedReader reader = new BufferedReader(in);
        Request request = new Request();
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            int bytesRead = 0;
            while (in.available() > 0 && (bytesRead = in.read(buffer)) != -1) {
                bytesOut.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray());

        try {
            parseRequest(bytesIn, request);
            parseHost(bytesIn, request);
            parseHeader(bytesIn, request);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return request;
    }

    private void parseRequest(InputStream in, Request request) throws IOException, URISyntaxException {
        StringBuffer line = new StringBuffer();
        readLine(in, line);
        if (line.length() == 0) {
            return;
        }
        LOGGER.trace(line.toString());
        StringTokenizer tokenizer = new StringTokenizer(line.toString());
        request.setMethod(Method.valueOf(tokenizer.nextToken()));
        request.setRequestUri(new URI(tokenizer.nextToken()));

        String httpVersionString = tokenizer.nextToken();
        request.setVersion(HTTPVersion.parseString(httpVersionString.substring(httpVersionString.indexOf("/") + 1)));
    }

    private void parseHost(ByteArrayInputStream in, Request request) throws IOException, URISyntaxException {
        StringBuffer line = new StringBuffer();
        readLine(in, line);
        if (line.length() == 0) {
            return;
        }
        LOGGER.trace(line.toString());
        StringTokenizer tokenizer = new StringTokenizer(line.toString());
        tokenizer.nextToken();
        String host = tokenizer.nextToken();
        request.setHost(new URI(host));
    }

    private void parseHeader(ByteArrayInputStream in, Request request) throws IOException {
        Header header = new Header();
        StringBuffer buffer = new StringBuffer();
        readLine(in, buffer);
        String line = buffer.toString();
        while (line != null) {
            System.out.println(line);
            if (line.startsWith(Constants.ACCEPT_MIMETYPE)) {

                StringTokenizer tokenizer = new StringTokenizer(line, ":,");
                tokenizer.nextToken();
                while (tokenizer.hasMoreTokens()) {
                    QualityParameter qualityParameter = nextQualityParameter(tokenizer);
                    if (line.startsWith(Constants.ACCEPT_CHARSET)) {
                        header.addAcceptCharset(qualityParameter.value, qualityParameter.quality);
                    } else if (line.startsWith(Constants.ACCEPT_ENCODING)) {
                        header.addAcceptEncoding(qualityParameter.value, qualityParameter.quality);
                    } else if (line.startsWith(Constants.ACCEPT_LANGUAGE)) {
                        header.addAcceptLanguage(qualityParameter.value, qualityParameter.quality);
                    } else if (line.startsWith(Constants.ACCEPT_MIMETYPE)) {
                        header.addAcceptMimetype(qualityParameter.value, qualityParameter.quality);
                    }
                }
            } else if (line.startsWith(Constants.COOKIE)) {
                StringTokenizer tokenizer = new StringTokenizer(line, " :;");
                tokenizer.nextToken();
                while (tokenizer.hasMoreTokens()) {
                    String nextCookieString = tokenizer.nextToken();
                    Cookie cookie = new Cookie();
                    int index = nextCookieString.indexOf("=");
                    cookie.setName(nextCookieString.substring(0, index).trim());
                    cookie.setValue(nextCookieString.substring(index + 1).trim());
                    header.addCookie(cookie);
                }
            } else if (line.startsWith("Content")) {
                Content content = request.getContent();
                StringTokenizer tokenizer = new StringTokenizer(line, ":;");
                String contentString = tokenizer.nextToken();
                if (Constants.CONTENT_TYPE.equals(contentString)) {

                } else if (Constants.CONTENT_LENGTH.equals(contentString)) {
                    String contentLengthString = tokenizer.nextToken().trim();
                    int contentLength = Integer.parseInt(contentLengthString);
                    content.setLength(contentLength);
                }
            } else if (line.isEmpty()) {
                Content content = request.getContent();
                if (content.getLength() < 0) {
                    break;
                }
                    byte[] buffer2 = new byte[content.getLength()];
                    if (in.available() > 0) {
                        in.read(buffer2);
                        content.setContent(buffer2);
                    } else {
                        break;
                    }
            } else {
                StringTokenizer tokenizer = new StringTokenizer(line, " :");
                header.addParameter(tokenizer.nextToken().trim(), tokenizer.nextToken().trim());
            }
            readLine(in, buffer);
            line = buffer.toString();
        }
        request.setHeader(header);
    }

    private QualityParameter nextQualityParameter(StringTokenizer tokenizer) {
        String nextMimetype = tokenizer.nextToken();
        int qualityIndex = nextMimetype.indexOf(";");
        String mimetype = nextMimetype;
        double quality = 1.0;
        if (qualityIndex != -1) {
            mimetype = nextMimetype.substring(0, qualityIndex);
            quality = Double.parseDouble(nextMimetype.substring(nextMimetype.lastIndexOf("=") + 1));
        }
        return new QualityParameter(mimetype.trim(), quality);
    }

    private static final int CR = 13;
    private static final int LF = 10;
    private int last = -1; // The last char we've read

    /**
     * Read a line of data from the underlying inputstream
     *
     * @return a line stripped of line terminators
     */
    private void readLine(InputStream in, StringBuffer sb) throws IOException {
        sb.delete(0, sb.length());
        int ch = -1;   // currently read char

        if (last != -1) sb.append((char) last);
        ch = in.read();
        if (ch == -1) {
            return;
        }
        while (ch != CR && ch != LF) {
            sb.append((char) ch);
            ch = in.read();
        }
        // Read the next byte and check if it's a LF
        last = in.read();
        if (last == LF) {
            last = -1;
        }
    }
}
