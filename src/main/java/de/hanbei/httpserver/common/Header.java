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

public class Header {

    private Map<String, Double> acceptMimetype;
    private Map<String, Double> acceptCharset;
    private Map<String, Double> acceptEncoding;
    private Map<String, Double> acceptLanguage;

    private List<Cookie> cookies;

    private Map<String, String> parameter;

    public Header() {
        this.acceptMimetype = new LinkedHashMap<String, Double>();
        this.acceptCharset = new LinkedHashMap<String, Double>();
        this.acceptEncoding = new LinkedHashMap<String, Double>();
        this.acceptLanguage = new LinkedHashMap<String, Double>();
        this.parameter = new LinkedHashMap<String, String>();
        this.cookies = new ArrayList<Cookie>();
    }

    public Set<String> getAcceptMimetypes() {
        return Collections.unmodifiableSet(this.acceptMimetype.keySet());
    }

    public void addAcceptMimetype(String mimetype) {
        this.acceptMimetype.put(mimetype, 1.0);
    }

    public void addAcceptMimetype(String mimetype, double quality) {
        this.acceptMimetype.put(mimetype, quality);
    }

    public Set<String> getAcceptCharset() {
        return Collections.unmodifiableSet(this.acceptCharset.keySet());
    }

    public void addAcceptCharset(String charset) {
        this.acceptCharset.put(charset, 1.0);
    }

    public void addAcceptCharset(String charset, double quality) {
        this.acceptCharset.put(charset, quality);
    }

    public Set<String> getAcceptEncoding() {
        return Collections.unmodifiableSet(this.acceptEncoding.keySet());
    }

    public void addAcceptEncoding(String encoding, double quality) {
        this.acceptEncoding.put(encoding, quality);
    }

    public void addAcceptEncoding(String encoding) {
        this.acceptEncoding.put(encoding, 1.0);
    }

    public Set<String> getAcceptLanguages() {
        return Collections.unmodifiableSet(this.acceptLanguage.keySet());
    }

    public void addAcceptLanguage(String language, double quality) {
        this.acceptLanguage.put(language, quality);
    }

    public void addAcceptLanguage(String language) {
        this.acceptLanguage.put(language, 1.0);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        addAcceptParameterPart(Constants.ACCEPT_MIMETYPE, acceptMimetype, buffer);

        addAcceptParameterPart(Constants.ACCEPT_LANGUAGE, acceptLanguage, buffer);

        addAcceptParameterPart(Constants.ACCEPT_ENCODING, acceptEncoding, buffer);

        addAcceptParameterPart(Constants.ACCEPT_CHARSET, acceptCharset, buffer);

        if (!parameter.isEmpty()) {
            for (String parameterKey : parameter.keySet()) {
                buffer.append(parameterKey);
                buffer.append(":");
                buffer.append(parameter.get(parameterKey));
            }
            buffer.append("\n");
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

    private void addAcceptParameterPart(String parameterName, Map<String, Double> acceptMap, StringBuffer buffer) {
        if (acceptMap.isEmpty()) {
            return;
        }
        buffer.append(parameterName);
        buffer.append(": ");
        int counter = 1;
        for (String value : acceptMap.keySet()) {
            buffer.append(value);
            if (acceptMap.get(value) != 1.0) {
                buffer.append(";q=");
                buffer.append(String.format(Locale.ENGLISH, "%1.1f", acceptMap.get(value)));
            }
            if (counter != acceptMap.size()) {
                buffer.append(",");
            }
            counter++;
        }
        buffer.append("\n");
    }

    public double getAcceptMimetypeQuality(String mimetype) {
        return getQualityParameter(acceptMimetype, mimetype);
    }

    public double getAcceptLanguageQuality(String language) {
        return getQualityParameter(acceptLanguage, language);
    }

    public double getAcceptEncodingQuality(String encoding) {
        return getQualityParameter(acceptEncoding, encoding);
    }

    public double getAcceptCharsetQuality(String charset) {
        return getQualityParameter(acceptCharset, charset);
    }

    private double getQualityParameter(Map<String, Double> acceptParameter, String key) {
        Double quality = acceptParameter.get(key);
        return quality != null ? quality : -1.0;
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    public void addParameter(String parameter, String value) {
        this.parameter.put(parameter, value);
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

}

