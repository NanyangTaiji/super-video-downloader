// Proxy.java
package com.myAllVideoBrowser.data.local.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Proxy {
    private final String id;
    private final String host;
    private final String port;
    private final String user;
    private final String password;
    private final boolean valid;
    private final String lastVerify;
    private final String countryCode;
    private final String cityName;
    private final String createdAt;

    public Proxy() {
        this("", "", "", "", "", false, "", "", "", "");
    }

    public Proxy(String id, String host, String port, String user, String password,
                 boolean valid, String lastVerify, String countryCode, String cityName, String createdAt) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.valid = valid;
        this.lastVerify = lastVerify;
        this.countryCode = countryCode;
        this.cityName = cityName;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getHost() { return host; }
    public String getPort() { return port; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public boolean isValid() { return valid; }
    public String getLastVerify() { return lastVerify; }
    public String getCountryCode() { return countryCode; }
    public String getCityName() { return cityName; }
    public String getCreatedAt() { return createdAt; }

    // Static factory methods
    public static Proxy noProxy() {
        return new Proxy();
    }

    public static Proxy fromServerMap(Map<?, ?> tmp) {
        if (tmp == null || tmp.isEmpty()) {
            return noProxy();
        }

        return new Proxy(
                getStringValue(tmp, "id"),
                getStringValue(tmp, "proxy_address").replace("null", "").trim(),
                getStringValue(tmp, "port").replace(".0", "").trim(),
                getStringValue(tmp, "username"),
                getStringValue(tmp, "password"),
                "true".equals(getStringValue(tmp, "valid")),
                getStringValue(tmp, "last_verification"),
                getStringValue(tmp, "country_code"),
                getStringValue(tmp, "city_name"),
                getStringValue(tmp, "created_at")
        );
    }

    public static Proxy fromMap(Map<?, ?> tmp) {
        if (tmp == null || tmp.isEmpty()) {
            return noProxy();
        }

        return new Proxy(
                getStringValue(tmp, "id"),
                getStringValue(tmp, "host").replace("null", "").trim(),
                getStringValue(tmp, "port").replace(".0", "").replace("null", "").trim(),
                getStringValue(tmp, "user").replace("null", "").trim(),
                getStringValue(tmp, "password").replace("null", "").trim(),
                "true".equals(getStringValue(tmp, "valid")),
                getStringValue(tmp, "lastVerification"),
                getStringValue(tmp, "countryCode"),
                getStringValue(tmp, "cityName"),
                getStringValue(tmp, "createdAt")
        );
    }

    // Helper method to safely extract string values from map
    private static String getStringValue(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    // Instance methods
    public Map<String, String> toMap() {
        Map<String, String> proxyMap = new HashMap<>();

        proxyMap.put("id", id);
        proxyMap.put("host", host);
        proxyMap.put("port", port);
        proxyMap.put("user", user);
        proxyMap.put("password", password);
        proxyMap.put("countryCode", countryCode);
        proxyMap.put("valid", String.valueOf(valid));
        proxyMap.put("lastVerify", lastVerify);
        proxyMap.put("cityName", cityName);
        proxyMap.put("createdAt", createdAt);

        return proxyMap;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Proxy proxy = (Proxy) other;
        return Objects.equals(port, proxy.port) && Objects.equals(host, proxy.host);
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", valid=" + valid +
                ", lastVerify='" + lastVerify + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
