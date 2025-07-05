package com.myAllVideoBrowser.util;

import android.content.Context;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.myAllVideoBrowser.R;

import javax.inject.Inject;

public class SystemUtil {

    @Inject
    public SystemUtil() {}

    public void clearCookies(Context context) {
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        if (context != null) {
            Toast.makeText(context, context.getString(R.string.cookies_cleared), Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
