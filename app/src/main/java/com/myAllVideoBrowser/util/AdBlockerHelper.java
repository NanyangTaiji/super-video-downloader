package com.myAllVideoBrowser.util;

import android.webkit.WebResourceResponse;
import java.io.ByteArrayInputStream;
import java.util.regex.Pattern;

public final class AdBlockerHelper {

    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }

    public static String parseAdsLine(String line) {
        if (line == null) return "";

        String a = line
                .replace("^$\\third-party", "")
                .replace("0.0.0.0", "")
                .replace(":::::", "")
                .replace(":", "")
                .replace("127.0.0.1", "")
                .replace("255.255.255.255", "")
                .replace("localhost", "")
                .trim()
                .toLowerCase()
                .replaceAll(" \\.{1,2} ", "");

        a = a.replace("www.", "").replace(".m", "").trim().toLowerCase().trim();

        if (a.startsWith(".") || a.startsWith("ip6-")) {
            return "";
        }
        return a;
    }
}
