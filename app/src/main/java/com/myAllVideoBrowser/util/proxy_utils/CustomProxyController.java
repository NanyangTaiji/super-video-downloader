// CustomProxyController.java
package com.myAllVideoBrowser.util.proxy_utils;

import androidx.webkit.ProxyConfig;
import androidx.webkit.ProxyController;
import androidx.webkit.WebViewFeature;
import com.myAllVideoBrowser.data.local.model.Proxy;
import com.myAllVideoBrowser.util.AppLogger;
import com.myAllVideoBrowser.util.SharedPrefHelper;
import com.myAllVideoBrowser.util.scheduler.BaseSchedulers;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import javax.inject.Inject;

public class CustomProxyController {

    private final SharedPrefHelper sharedPrefHelper;
    private final BaseSchedulers schedulers;

    @Inject
    public CustomProxyController(SharedPrefHelper sharedPrefHelper, BaseSchedulers schedulers) {
        this.sharedPrefHelper = sharedPrefHelper;
        this.schedulers = schedulers;

        if (isProxyOn()) {
            setCurrentProxy(getCurrentRunningProxy());
        }
    }

    public Proxy getCurrentRunningProxy() {
        if (isProxyOn()) {
            return sharedPrefHelper.getCurrentProxy();
        } else {
            return Proxy.noProxy();
        }
    }

    public Proxy getCurrentSavedProxy() {
        return sharedPrefHelper.getCurrentProxy();
    }

    public android.util.Pair<String, String> getProxyCredentials() {
        Proxy currProxy = getCurrentRunningProxy();
        return new android.util.Pair<>(currProxy.getUser(), currProxy.getPassword());
    }

    public void setCurrentProxy(Proxy proxy) {
        if (proxy.equals(Proxy.noProxy())) {
            System.setProperty("http.proxyUser", "");
            System.setProperty("http.proxyPassword", "");
            System.setProperty("https.proxyUser", "");
            System.setProperty("https.proxyPassword", "");

            Authenticator.setDefault(new Authenticator() {});

            if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
                ProxyController.getInstance().clearProxyOverride(Runnable::run, () -> {});
            }
        } else {
            sharedPrefHelper.setIsProxyOn(true);

            System.setProperty("http.proxyUser", proxy.getUser().trim());
            System.setProperty("http.proxyPassword", proxy.getPassword().trim());
            System.setProperty("https.proxyUser", proxy.getUser().trim());
            System.setProperty("https.proxyPassword", proxy.getPassword().trim());

            System.setProperty("http.proxyHost", proxy.getHost().trim());
            System.setProperty("http.proxyPort", proxy.getPort().trim());

            System.setProperty("https.proxyHost", proxy.getHost().trim());
            System.setProperty("https.proxyPort", proxy.getPort().trim());
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxy.getUser(), proxy.getPassword().toCharArray());
                }
            });

            ProxyConfig proxyConfig = new ProxyConfig.Builder()
                    .addProxyRule(proxy.getHost() + ":" + proxy.getPort())
                    .build();

            if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
                try {
                    ProxyController.getInstance().setProxyOverride(proxyConfig,
                            Runnable::run, () -> {});
                } catch (Exception e) {
                    AppLogger.d("ERROR SETTING PROXY: " + e);
                }
            }
        }

        sharedPrefHelper.setCurrentProxy(proxy);
    }

    public @NonNull Observable<Object> fetchUserProxy() {
        return Observable.create(emitter -> {
            Proxy userProxy = sharedPrefHelper.getUserProxy();
            if (userProxy != null) {
                emitter.onNext(userProxy);
                emitter.onComplete();
            } else {
                emitter.onComplete();
            }
        }).doOnError(throwable -> {}).subscribeOn(schedulers.getIo());
    }

    public boolean isProxyOn() {
        return sharedPrefHelper.getIsProxyOn();
    }

    public void setIsProxyOn(boolean isOn) {
        if (isOn) {
            setCurrentProxy(sharedPrefHelper.getCurrentProxy());
        } else {
            System.setProperty("http.proxyUser", "");
            System.setProperty("http.proxyPassword", "");
            System.setProperty("https.proxyUser", "");
            System.setProperty("https.proxyPassword", "");

            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");

            System.setProperty("https.proxyHost", "");
            System.setProperty("https.proxyPort", "");
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

            Authenticator.setDefault(new Authenticator() {});

            if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
                ProxyController.getInstance().clearProxyOverride(Runnable::run, () -> {});
            }
        }

        sharedPrefHelper.setIsProxyOn(isOn);
    }
}
