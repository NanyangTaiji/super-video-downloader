package com.myAllVideoBrowser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.google.gson.Gson;
import com.myAllVideoBrowser.data.local.model.Proxy;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefHelper {

    public static final String PREF_KEY = "settings_prefs";
    private static final String IS_DESKTOP = "IS_DESKTOP";
    private static final String IS_FIND_BY_URL = "IS_FIND_BY_URL";
    private static final String IS_CHECK_EVERY_REQUEST = "IS_CHECK_EVERY_REQUEST";
    private static final String IS_AD_BLOCKER = "IS_AD_BLOCKER";
    private static final String PROXY_IP_PORT = "PROXY_IP_PORT";
    private static final String IS_PROXY_TURN_ON = "IS_PROXY_TURN_ON";
    private static final String IS_FIRST_START = "IS_FIRST_START";
    private static final String IS_SHOW_VIDEO_ALERT = "IS_SHOW_VIDEO_ALERT";
    private static final String IS_SHOW_VIDEO_ACTION_BUTTON = "IS_SHOW_VIDEO_ACTION_BUTTON";
    private static final String IS_PRESENT = "IS_PRESENT";
    private static final String HOSTS_UPDATE = "HOSTS_UPDATE";
    private static final String HOSTS_POPULATED = "HOSTS_POPULATED";
    private static final String IS_EXTERNAL_USE = "IS_EXTERNAL_USE";
    private static final String IS_APP_DIR_USE = "IS_APP_DIR_USE";
    private static final String IS_DARK_MODE = "IS_DARK_MODE";
    public static final String REGULAR_THREAD_COUNT = "REGULAR_THREAD_COUNT";
    private static final String M3U8_THREAD_COUNT = "M3U8_THREAD_COUNT";
    private static final String VIDEO_DETECTION_TRESHOLD = "VIDEO_DETECTION_TRESHOLD";
    private static final String IS_LOCK_PORTRAIT = "IS_LOCK_PORTRAIT";
    private static final String USER_PROXY = "USER_PROXY";
    private static final String IS_CHECK_EVERY_ON_M3U8 = "IS_CHECK_EVERY_ON_M3U8";
    private static final String IS_AUTO_THEME = "IS_AUTO_THEME";
    private static final String IS_CHECK_ON_AUDIO = "IS_CHECK_ON_AUDIO";

    private final Context context;
    private final AppUtil appUtil;
    private final Gson gson;
    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedPrefHelper(Context context, AppUtil appUtil) {
        this.context = context;
        this.appUtil = appUtil;
        this.gson = new Gson();
        this.sharedPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    public void saveIsDesktop(boolean isDesktop) {
        sharedPreferences.edit()
                .putBoolean(IS_DESKTOP, isDesktop)
                .apply();
    }

    public boolean getIsDesktop() {
        return sharedPreferences.getBoolean(IS_DESKTOP, false);
    }

    public void saveIsFindByUrl(boolean isFind) {
        sharedPreferences.edit()
                .putBoolean(IS_FIND_BY_URL, isFind)
                .apply();
    }

    public boolean isFindVideoByUrl() {
        return sharedPreferences.getBoolean(IS_FIND_BY_URL, true);
    }

    public void saveIsCheck(boolean isCheck) {
        sharedPreferences.edit()
                .putBoolean(IS_CHECK_EVERY_REQUEST, isCheck)
                .apply();
    }

    public boolean isCheckEveryRequestOnVideo() {
        return sharedPreferences.getBoolean(IS_CHECK_EVERY_REQUEST, true);
    }

    public void saveIsAdBlocker(boolean isAdBlocker) {
        sharedPreferences.edit()
                .putBoolean(IS_AD_BLOCKER, isAdBlocker)
                .apply();
    }

    public boolean getIsAdBlocker() {
        return sharedPreferences.getBoolean(IS_AD_BLOCKER, true);
    }

    public void setCurrentProxy(Proxy proxy) {
        sharedPreferences.edit()
                .putString(PROXY_IP_PORT, gson.toJson(proxy.toMap()))
                .apply();
    }

    public Proxy getCurrentProxy() {
        String value = sharedPreferences.getString(PROXY_IP_PORT, "{}");
        if (value == null) value = "{}";
        Map<String, Object> tmp = gson.fromJson(value, Map.class);
        return Proxy.fromMap(tmp);
    }

    public boolean getIsProxyOn() {
        return sharedPreferences.getBoolean(IS_PROXY_TURN_ON, false);
    }

    public void setIsProxyOn(boolean isTurnedOn) {
        sharedPreferences.edit()
                .putBoolean(IS_PROXY_TURN_ON, isTurnedOn)
                .apply();
    }

    public boolean getIsFirstStart() {
        return sharedPreferences.getBoolean(IS_FIRST_START, true);
    }

    public void setIsFirstStart(boolean isFirstStart) {
        sharedPreferences.edit()
                .putBoolean(IS_FIRST_START, isFirstStart)
                .apply();
    }

    public boolean isShowVideoAlert() {
        return sharedPreferences.getBoolean(IS_SHOW_VIDEO_ALERT, true);
    }

    public void setIsShowVideoAlert(boolean isShow) {
        sharedPreferences.edit()
                .putBoolean(IS_SHOW_VIDEO_ALERT, isShow)
                .apply();
    }

    public boolean isShowActionButton() {
        return sharedPreferences.getBoolean(IS_SHOW_VIDEO_ACTION_BUTTON, true);
    }

    public void setIsShowActionButton(boolean isShow) {
        sharedPreferences.edit()
                .putBoolean(IS_SHOW_VIDEO_ACTION_BUTTON, isShow)
                .apply();
    }

    public boolean getIsPresent() {
        return sharedPreferences.getBoolean(IS_PRESENT, true);
    }

    public void setIsPresent(boolean isPresent) {
        sharedPreferences.edit()
                .putBoolean(IS_PRESENT, isPresent)
                .apply();
    }

    public void setIsAdHostsUpdateTime(long time) {
        sharedPreferences.edit()
                .putLong(HOSTS_UPDATE, time)
                .apply();
    }

    public long getAdHostsUpdateTime() {
        return sharedPreferences.getLong(HOSTS_UPDATE, 0);
    }

    public boolean getIsPopulated() {
        return sharedPreferences.getBoolean(HOSTS_POPULATED, false);
    }

    public void setIsPopulated(boolean isPopulated) {
        sharedPreferences.edit()
                .putBoolean(HOSTS_POPULATED, isPopulated)
                .apply();
    }

    public boolean getIsExternalUse() {
        boolean defIsExternal = FileUtil.isExternalStorageWritable();
        return sharedPreferences.getBoolean(IS_EXTERNAL_USE, defIsExternal);
    }

    public void setIsExternalUse(boolean isExternalUse) {
        sharedPreferences.edit()
                .putBoolean(IS_EXTERNAL_USE, isExternalUse)
                .apply();
    }

    public boolean getIsAppDirUse() {
        return sharedPreferences.getBoolean(IS_APP_DIR_USE, false);
    }

    public void setIsAppDirUse(boolean isAppDirUse) {
        sharedPreferences.edit()
                .putBoolean(IS_APP_DIR_USE, isAppDirUse)
                .apply();
    }

    public boolean isDarkMode() {
        boolean isNightMode = appUtil.getSystemDefaultThemeIsDark(context);

        if (isAutoTheme()) {
            return isNightMode;
        }

        return sharedPreferences.getBoolean(IS_DARK_MODE, isNightMode);
    }

    public boolean isAutoTheme() {
        boolean isAuto = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        return sharedPreferences.getBoolean(IS_AUTO_THEME, isAuto);
    }

    public void setIsAutoTheme(boolean isAuto) {
        sharedPreferences.edit()
                .putBoolean(IS_AUTO_THEME, isAuto)
                .apply();
    }

    public void setIsDarkMode(boolean isDarkMode) {
        sharedPreferences.edit()
                .putBoolean(IS_DARK_MODE, isDarkMode)
                .apply();
    }

    public int getRegularDownloaderThreadCount() {
        return sharedPreferences.getInt(REGULAR_THREAD_COUNT, 1);
    }

    public void setRegularDownloaderThreadCount(int count) {
        sharedPreferences.edit()
                .putInt(REGULAR_THREAD_COUNT, count)
                .apply();
    }

    public int getM3u8DownloaderThreadCount() {
        return sharedPreferences.getInt(M3U8_THREAD_COUNT, 3); // means 4
    }

    public void setM3u8DownloaderThreadCount(int count) {
        sharedPreferences.edit()
                .putInt(M3U8_THREAD_COUNT, count)
                .apply();
    }

    public int getVideoDetectionTreshold() {
        return sharedPreferences.getInt(VIDEO_DETECTION_TRESHOLD, 5 * 1024 * 1024);
    }

    public void setVideoDetectionTreshold(int count) {
        sharedPreferences.edit()
                .putInt(VIDEO_DETECTION_TRESHOLD, count)
                .apply();
    }

    public boolean getIsLockPortrait() {
        return sharedPreferences.getBoolean(IS_LOCK_PORTRAIT, false);
    }

    public void setIsLockPortrait(boolean isLock) {
        sharedPreferences.edit()
                .putBoolean(IS_LOCK_PORTRAIT, isLock)
                .apply();
    }

    public Proxy getUserProxy() {
        String proxyString = sharedPreferences.getString(USER_PROXY, "");
        if (proxyString != null && !proxyString.isEmpty()) {
            return gson.fromJson(proxyString, Proxy.class);
        }

        return Proxy.noProxy();
    }

    public void saveUserProxy(Proxy proxy) {
        String proxyString = gson.toJson(proxy);
        sharedPreferences.edit()
                .putString(USER_PROXY, proxyString)
                .apply();
    }

    public boolean getIsCheckEveryOnM3u8() {
        return sharedPreferences.getBoolean(IS_CHECK_EVERY_ON_M3U8, true);
    }

    public void saveIsCheckEveryOnM3u8(boolean isCheck) {
        sharedPreferences.edit()
                .putBoolean(IS_CHECK_EVERY_ON_M3U8, isCheck)
                .apply();
    }

    public boolean getIsCheckOnAudio() {
        return sharedPreferences.getBoolean(IS_CHECK_ON_AUDIO, true);
    }

    public void saveIsCheckOnAudio(boolean isCheck) {
        sharedPreferences.edit()
                .putBoolean(IS_CHECK_ON_AUDIO, isCheck)
                .apply();
    }
}
