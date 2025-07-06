package com.myAllVideoBrowser.util.proxy_utils;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import java.net.InetSocketAddress;
import java.net.Proxy;
import javax.inject.Inject;

public class OkHttpProxyClient {

    private final OkHttpClient okHttpClient;
    private final CustomProxyController proxyController;
    private com.myAllVideoBrowser.data.local.model.Proxy currentProxy;
    private OkHttpClient httpClientCached;

    @Inject
    public OkHttpProxyClient(OkHttpClient okHttpClient, CustomProxyController proxyController) {
        this.okHttpClient = okHttpClient;
        this.proxyController = proxyController;
        this.currentProxy = getProxy();
    }

    public OkHttpClient getProxyOkHttpClient() {
        com.myAllVideoBrowser.data.local.model.Proxy proxy = getProxy();

        if (!proxy.getHost().equals(currentProxy.getHost()) ||
                !proxy.getPort().equals(currentProxy.getPort()) ||
                httpClientCached == null) {

            currentProxy = proxy;
            String proxyCredentials = getProxyCredentials();

            Authenticator proxyAuthenticator = (route, response) ->
                    response.request().newBuilder()
                            .header("Proxy-Authorization", proxyCredentials)
                            .build();

            if (proxy.equals(com.myAllVideoBrowser.data.local.model.Proxy.noProxy())) {
                httpClientCached = okHttpClient.newBuilder().build();
            } else {
                int port = 1;
                try {
                    port = Integer.parseInt(proxy.getPort());
                } catch (NumberFormatException e) {
                    port = 1;
                }

                httpClientCached = okHttpClient.newBuilder()
                        .proxy(new Proxy(Proxy.Type.HTTP,
                                new InetSocketAddress(proxy.getHost(), port)))
                        .proxyAuthenticator(proxyAuthenticator)
                        .build();
            }
        }

        return httpClientCached;
    }

    private com.myAllVideoBrowser.data.local.model.Proxy getProxy() {
        return proxyController.getCurrentRunningProxy();
    }

    private String getProxyCredentials() {
        android.util.Pair<String, String> creds = proxyController.getProxyCredentials();
        return Credentials.basic(creds.first, creds.second);
    }
}
