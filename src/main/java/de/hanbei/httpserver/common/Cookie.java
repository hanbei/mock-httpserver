package de.hanbei.httpserver.common;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 27.02.11
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class Cookie {

    private String name;
    private String value;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Cookie() {
        this("","");

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
        if (this == o) return true;
        if (!(o instanceof Cookie)) return false;

        Cookie cookie = (Cookie) o;

        if (name != null ? !name.equals(cookie.name) : cookie.name != null) return false;
        if (value != null ? !value.equals(cookie.value) : cookie.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
