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

/** Represents a cookie in a response and a request. Cookies have a name and a value. */
public class Cookie {

    private String name;
    private String value;

    /**
     * Create a cookie with a name and a value set.
     *
     * @param name  The name of the cookie.
     * @param value The value of the cookie.
     */
    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /** Create a empty cookie. */
    public Cookie() {
        this("", "");

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cookie)) {
            return false;
        }

        Cookie cookie = (Cookie) o;

        if (name != null) {
            if (!name.equals(cookie.name)) {
                return false;
            }
        } else {
            if (cookie.name != null) {
                return false;
            }
        }
        if (value != null) {
            if (!value.equals(cookie.value)) {
                return false;
            }
        } else {
            if (cookie.value != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
