package com.myAllVideoBrowser.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FaviconUtils {

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        }
        return stream.toByteArray();
    }

    public static Bitmap getEncodedFaviconFromUrl(OkHttpClient okHttpClient, String url) {
        return fetchFavicon(okHttpClient, url);
    }

    private static Bitmap fetchFavicon(OkHttpClient okHttpClient, String url) {
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (host == null) return null;

        String[] potentialUrls = {
                "https://" + host + "/favicon.ico",
                "https://" + host.replaceFirst("www\\.", "") + "/favicon.ico"
        };

        for (String reqUrl : potentialUrls) {
            Request request = new Request.Builder().url(reqUrl).build();
            try (okhttp3.Response response = okHttpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return BitmapFactory.decodeStream(response.body().byteStream());
                }
            } catch (IOException ignored) {}
        }
        return null;
    }
}