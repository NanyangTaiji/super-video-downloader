package com.myAllVideoBrowser.data.local.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Proxy {
    private String id = "";
    private String host = "";
    private String port = "";
    private String user = "";
    private String password = "";
    private boolean valid = false;
    private String lastVerify = "";
    private String countryCode = "";
    private String cityName = "";
    private String createdAt = "";

    public Proxy() {}

    public Proxy(String id, String host, String port, String user, String password,
                 boolean valid, String lastVerify, String countryCode,
                 String cityName, String createdAt) {
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

    public static Proxy noProxy() {
        return new Proxy();
    }

    public static Proxy fromServerMap(Map<?, ?> tmp) {
        return new Proxy(
                tmp.get("id").toString(),
                tmp.get("proxy_address").toString().replace("null", "").trim(),
                tmp.get("port").toString().replace(".0", "").trim(),
                tmp.get("username").toString(),
                tmp.get("password").toString(),
                "true".equals(tmp.get("valid").toString()),
                tmp.get("last_verification").toString(),
                tmp.get("country_code").toString(),
                tmp.get("city_name").toString(),
                tmp.get("created_at").toString()
        );
    }

    public static Proxy fromMap(Map<?, ?> tmp) {
        return new Proxy(
                tmp.get("id").toString(),
                tmp.get("host").toString().replace("null", "").trim(),
                tmp.get("port").toString().replace(".0", "").replace("null", "").trim(),
                tmp.get("user").toString().replace("null", "").trim(),
                tmp.get("password").toString().replace("null", "").trim(),
                "true".equals(tmp.get("valid").toString()),
                tmp.get("lastVerification").toString(),
                tmp.get("countryCode").toString(),
                tmp.get("cityName").toString(),
                tmp.get("createdAt").toString()
        );
    }

    public Map<String, String> toMap() {
        Map<String, String> proxyMap = new HashMap<>();
        proxyMap.put("id", id);
        proxyMap.put("host", host);
        proxyMap.put("port", port);
        proxyMap.put("user", user);
        proxyMap.put("password", password);
        proxyMap.put("countryCode", countryCode);
        proxyMap.put("valid", Boolean.toString(valid));
        proxyMap.put("lastVerify", lastVerify);
        proxyMap.put("cityName", cityName);
        proxyMap.put("createdAt", createdAt);
        return proxyMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proxy proxy = (Proxy) o;
        return port.equals(proxy.port) && host.equals(proxy.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getLastVerify() { return lastVerify; }
    public void setLastVerify(String lastVerify) { this.lastVerify = lastVerify; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
