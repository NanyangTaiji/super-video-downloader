package com.myAllVideoBrowser.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import okhttp3.Headers;
import okhttp3.Request;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CookieUtils {

    public static String chromeDefaultPathApi29More =
            ContextUtils.getApplicationContext().getFilesDir().getParentFile() + "/app_webview/Default/";

    public static String chromeDefaultPathApi28Less =
            ContextUtils.getApplicationContext().getFilesDir().getParentFile() + "/app_webview/";

    public static Request webRequestToHttpWithCookies(WebResourceRequest request) {
        String url = request.getUrl().toString();
        Map<String, String> tmpHeaders = request.getRequestHeaders();

        try {
            tmpHeaders.put("Cookie", CookieManager.getInstance().getCookie(url) != null ?
                    CookieManager.getInstance().getCookie(url) : "");
        } catch (Throwable e) {
            tmpHeaders.put("Cookie", "");
        }

        try {
            return new Request.Builder()
                    .headers(Headers.of(tmpHeaders))
                    .url(url)
                    .build();
        } catch (Throwable e) {
            return null;
        }
    }

    public static File addCookiesToRequest(
            String url,
            YoutubeDLRequest request,
            String additionalUrl
    ) {
        if (Build.VERSION.SDK_INT > 32) {
            File cookieFile = new File(chromeDefaultPathApi29More);
            if (cookieFile.exists() && !cookieFile.isFile()) {
                request.addOption("--cookies-from-browser", "chrome:" + cookieFile.getPath());
            }
            return cookieFile;
        }

        File cookieFile = createTmpCookieFile(String.valueOf(url.hashCode()));
        String cookies = readCookiesForUrlFromDb(url);

        if (additionalUrl != null && cookies.split("\n").length <= 3) {
            cookies = readCookiesForUrlFromDb(additionalUrl);
        }
        if (cookieFile.exists() && cookieFile.isFile()) {
            try (java.io.FileWriter writer = new java.io.FileWriter(cookieFile)) {
                writer.write(cookies);
            } catch (Exception e) { /* Handle error */ }
            request.addOption("--cookies", cookieFile.getPath());
        }
        return cookieFile;
    }

    public static String getCookiesForUrlNetScape(String url, String additionalUrl) {
        String cookies = readCookiesForUrlFromDb(url);
        if (additionalUrl != null && cookies.split("\n").length <= 3) {
            cookies = readCookiesForUrlFromDb(additionalUrl);
        }
        return cookies;
    }

    public static Pair<URL, Headers> getFinalRedirectURL(URL url, Map<String, String> headers) {
        Map<String, String> currentHeaders = new java.util.HashMap<>(headers);
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setInstanceFollowRedirects(false);
            for (Map.Entry<String, String> header : currentHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            try {
                con.connect();
            } catch (Throwable ignored) {}

            int resCode = con.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    resCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    resCode == HttpURLConnection.HTTP_MOVED_TEMP) {

                String location = con.getHeaderField("Location");
                String origin = con.getHeaderField("Access-Control-Allow-Origin");

                if (location.startsWith("/")) {
                    location = location.startsWith("//") ?
                            url.getProtocol() + ":" + location :
                            url.getProtocol() + "://" + url.getHost() + location;
                }
                if (origin != null) {
                    currentHeaders.put("Referer", origin);
                }
                return getFinalRedirectURL(new URL(location), currentHeaders);
            }
        } catch (Exception ignored) {}
        return new Pair<>(url, Headers.of(currentHeaders));
    }

    private static String readCookiesForUrlFromDb(String url) {
        File cookiesDbFile = (Build.VERSION.SDK_INT > 28) ?
                new File(chromeDefaultPathApi29More + "/Cookies") :
                new File(chromeDefaultPathApi28Less + "/Cookies");

        ChromeBrowser chrome = new ChromeBrowser();
        return chrome.getCookiesNetscapeForDomain(Uri.parse(url).getHost(), cookiesDbFile).trim();
    }

    private static File createTmpCookieFile(String name) {
        File file = new File(ContextUtils.getApplicationContext().getCacheDir(), name);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (Exception e) { /* Handle error */ }
        return file;
    }

    public static class Pair<A, B> {
        public final A first;
        public final B second;
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }


    public static class Cookie {
        private String name;
        private byte[] encryptedValue;
        private Date expires;
        private String path;
        private String domain;
        private boolean isSecure;
        private boolean isHttpOnly;
        private File cookieStore;

        public Cookie(String name, byte[] encryptedValue, Date expires, String path,
                      String domain, boolean secure, boolean httpOnly, File cookieStore) {
            this.name = name;
            this.encryptedValue = encryptedValue;
            this.expires = expires;
            this.path = path;
            this.domain = domain;
            this.isSecure = secure;
            this.isHttpOnly = httpOnly;
            this.cookieStore = cookieStore;
        }

        public boolean isDecrypted(){
            return false;
        };

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getEncryptedValue() {
            return encryptedValue;
        }

        public void setEncryptedValue(byte[] encryptedValue) {
            this.encryptedValue = encryptedValue;
        }

        public Date getExpires() {
            return expires;
        }

        public void setExpires(Date expires) {
            this.expires = expires;
        }

        public String getPath() {
            return path;
        }

        protected void setPath(String path) {
            this.path = path;
        }

        public String getDomain() {
            return domain;
        }

        protected void setDomain(String domain) {
            this.domain = domain;
        }

        public boolean isSecure() {
            return isSecure;
        }

        protected void setSecure(boolean secure) {
            isSecure = secure;
        }

        public boolean isHttpOnly() {
            return isHttpOnly;
        }

        protected void setHttpOnly(boolean httpOnly) {
            isHttpOnly = httpOnly;
        }

        public File getCookieStore() {
            return cookieStore;
        }

        protected void setCookieStore(File cookieStore) {
            this.cookieStore = cookieStore;
        }
    }

    public static class DecryptedCookie extends Cookie {
        private String decryptedValue;

        public DecryptedCookie(String name, byte[] encryptedValue, String decryptedValue,
                               Date expires, String path, String domain,
                               boolean isSecure, boolean isHttpOnly, File cookieStore) {
            super(name, encryptedValue, expires, path, domain, isSecure, isHttpOnly, cookieStore);
            this.decryptedValue = decryptedValue;
        }

        @Override
        public boolean isDecrypted() {
            return true;
        }
    }

    public static class EncryptedCookie extends Cookie {
        public EncryptedCookie(String name, byte[] encryptedValue, Date expires,
                               String path, String domain,
                               boolean isSecure, boolean isHttpOnly, File cookieStore) {
            super(name, encryptedValue, expires, path, domain, isSecure, isHttpOnly, cookieStore);
        }
    }

    public static abstract class Browser {
        protected File cookieStoreCopy;

        public Browser() {
            this.cookieStoreCopy = new File(
                    ContextUtils.getApplicationContext().getCacheDir(),
                    "cookies_" + this.hashCode() + ".db"
            );
        }

        public abstract Set<File> getCookieStores();
        public abstract Set<Cookie> processCookies(File cookieStore, String domainFilter);
        protected abstract DecryptedCookie decrypt(EncryptedCookie encryptedCookie);
    }

    public static class ChromeBrowser extends Browser {
        private static final long CHROMEEPOCHSTART = 11644473600000L;
        private String chromeKeyringPassword;

        @Override
        public Set<File> getCookieStores() {
            Set<File> cookieStores = new java.util.HashSet<>();
            String cookiesDbPath = (Build.VERSION.SDK_INT > 28) ?
                    CookieUtils.chromeDefaultPathApi29More + "Cookies" :
                    CookieUtils.chromeDefaultPathApi28Less + "Cookies";
            cookieStores.add(new File(cookiesDbPath));
            return cookieStores;
        }

        public String getCookiesNetscapeForDomain(String domain, File cookiesStore) {
            return processCookiesToNetscape(cookiesStore, domain);
        }

        @Override
        public Set<Cookie> processCookies(File cookieStore, String domainFilter) {
            return new java.util.HashSet<>();
        }

        private String processCookiesToNetscape(File cookieStore, String domainFilter) {
            StringBuilder netscapeCookieFile = new StringBuilder();
            netscapeCookieFile.append("# Netscape HTTP Cookie File\n");
            netscapeCookieFile.append("# https://curl.haxx.se/rfc/cookie_spec.html\n");
            netscapeCookieFile.append("# This is a generated file! Do not edit.\n\n");

            if (cookieStore != null && cookieStore.exists()) {
                File cookieStoreCopy = new File(
                        ContextUtils.getApplicationContext().getCacheDir(),
                        "cookieStoreCopy.db"
                );

                try {
                    // Copy file logic would go here
                    SQLiteDatabase db = SQLiteDatabase.openDatabase(
                            cookieStoreCopy.getAbsolutePath(),
                            null,
                            SQLiteDatabase.OPEN_READONLY
                    );

                    String query = "SELECT * FROM cookies";
                    if (domainFilter != null && !domainFilter.isEmpty()) {
                        query += " WHERE host_key LIKE '%" + domainFilter + "%'";
                    }

                    Cursor cursor = db.rawQuery(query, null);
                    if (cursor != null) {
                        try {
                            while (cursor.moveToNext()) {
                                Map<String, Object> cookieData = extractCookieData(cursor);
                                netscapeCookieFile.append(formatCookieLine(cookieData)).append("\n");
                            }
                        } finally {
                            cursor.close();
                        }
                    }
                    db.close();
                } catch (Exception e) {
                    Log.e("ChromeBrowser", "Error processing cookies", e);
                } finally {
                    cookieStoreCopy.delete();
                }
            }
            return netscapeCookieFile.toString();
        }

        private Map<String, Object> extractCookieData(Cursor cursor) {
            Map<String, Object> cookieData = new HashMap<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                int columnType = cursor.getType(i);
                switch (columnType) {
                    case Cursor.FIELD_TYPE_BLOB:
                        cookieData.put(columnName, cursor.getBlob(i));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        cookieData.put(columnName, cursor.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        cookieData.put(columnName, cursor.getLong(i));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        cookieData.put(columnName, cursor.getString(i));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        cookieData.put(columnName, null);
                        break;
                    default:
                        cookieData.put(columnName, cursor.getString(i));
                }
            }
            return cookieData;
        }

        private String formatCookieLine(Map<String, Object> cookieData) {
            String name = (String) cookieData.get("name");
            byte[] encryptedBytes = (byte[]) cookieData.get("encrypted_value");
            String value = (String) cookieData.get("value");
            String path = (String) cookieData.get("path");
            String domain = (String) cookieData.get("host_key");
            long secureLong = (Long) cookieData.getOrDefault("is_secure",
                    cookieData.getOrDefault("secure", 0L));
            boolean secure = secureLong == 1L;
            long httpOnlyLong = (Long) cookieData.getOrDefault("is_httponly",
                    cookieData.getOrDefault("httponly", 0L));
            boolean httpOnly = httpOnlyLong == 1L;
            long expiresLong = (Long) cookieData.getOrDefault("expires_utc", 0L);
            long expires = chromeTime(expiresLong);

            String httpOnlyString = httpOnly ? "#HttpOnly_" : "";
            String isSubdomainString = domain.startsWith(".") ? "TRUE" : "FALSE";
            String isSecureString = secure ? "TRUE" : "FALSE";
            String expiresFormatted = (expires == 0) ? "0" : String.valueOf(expires);
            String valueFormatted = (encryptedBytes != null && encryptedBytes.length > 0 &&
                    (value == null || value.isEmpty())) ?
                    new String(encryptedBytes) : value;

            return String.format("%s%s\t%s\t%s\t%s\t%s\t%s",
                    httpOnlyString,
                    domain,
                    isSubdomainString,
                    path,
                    isSecureString,
                    expiresFormatted,
                    name,
                    valueFormatted);
        }

        private long chromeTime(long t) {
            return t / 1000 - CHROMEEPOCHSTART;
        }

        @Override
        protected DecryptedCookie decrypt(EncryptedCookie encryptedCookie) {
            return null;
        }
    }
}