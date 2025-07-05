package com.myAllVideoBrowser.util;

import com.myAllVideoBrowser.ui.main.home.browser.ContentType;
import com.myAllVideoBrowser.util.proxy_utils.OkHttpProxyClient;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class VideoUtils {

    private static final Pattern REGEX_PATTERN = Pattern.compile("\\.(js|css|m4s|ts)$|^blob:");

    public static ContentType getContentTypeByUrl(
            String url,
            Headers headers,
            OkHttpProxyClient okHttpProxyClient
    ) {
        if (REGEX_PATTERN.matcher(url).find()) {
            return ContentType.OTHER;
        }

        Request request = new Request.Builder()
                .url(url)
                .headers(headers != null ? headers : Headers.of())
                .get()
                .build();

        try (Response response = okHttpProxyClient.getProxyOkHttpClient().newCall(request).execute()) {
            String contentTypeStr = response.header("Content-Type");

            if (contentTypeStr != null && contentTypeStr.contains("mpegurl")) {
                return ContentType.M3U8;
            } else if (contentTypeStr != null && contentTypeStr.contains("dash")) {
                return ContentType.MPD;
            } else if (contentTypeStr != null && contentTypeStr.contains("video")) {
                return ContentType.VIDEO;
            } else if (contentTypeStr != null && contentTypeStr.toLowerCase().contains("audio")) {
                return ContentType.AUDIO;
            } else if (contentTypeStr != null && contentTypeStr.contains("application/octet-stream")) {
                ResponseBody body = response.body();
                if (body != null) {
                    try (InputStreamReader reader = new InputStreamReader(body.byteStream())) {
                        char[] buffer = new char[7];
                        int charsRead = reader.read(buffer, 0, 7);
                        if (charsRead > 0) {
                            String content = new String(buffer, 0, charsRead);
                            if (content.startsWith("#EXTM3U")) {
                                return ContentType.M3U8;
                            } else if (content.contains("<MPD")) {
                                return ContentType.MPD;
                            }
                        }
                    }
                }
                return ContentType.OTHER;
            } else {
                return ContentType.OTHER;
            }
        } catch (IOException e) {
            return ContentType.OTHER;
        }
    }
}
