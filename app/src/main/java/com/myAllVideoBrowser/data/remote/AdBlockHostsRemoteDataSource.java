package com.myAllVideoBrowser.data.remote;

import com.myAllVideoBrowser.data.local.room.entity.AdHost;
import com.myAllVideoBrowser.data.repository.AdBlockHostsRepository;
import com.myAllVideoBrowser.util.AdBlockerHelper;
import com.myAllVideoBrowser.util.proxy_utils.OkHttpProxyClient;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdBlockHostsRemoteDataSource implements AdBlockHostsRepository {

    private static final String AD_HOSTS_URL_LIST_ADAWAY = "https://adaway.org/hosts.txt";
    private static final String AD_HOSTS_URLS_LIST_ADMIRAL = "https://v.firebog.net/hosts/Admiral.txt";
    private static final String TRACKING_BLACK_LIST = "https://v.firebog.net/hosts/Easyprivacy.txt";
    private static final String AD_HOSTS_URLS_AD_GUARD = "https://v.firebog.net/hosts/AdguardDNS.txt";

    private final OkHttpProxyClient okHttpClient;
    private final Pattern domainPattern = Pattern.compile(".+\\..+");

    @Inject
    public AdBlockHostsRemoteDataSource(OkHttpProxyClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public CompletableFuture<Set<AdHost>> fetchHosts() {
        CompletableFuture<Set<AdHost>> task1 = fetchListFromUrl(AD_HOSTS_URLS_AD_GUARD);
        CompletableFuture<Set<AdHost>> task2 = fetchListFromUrl(AD_HOSTS_URL_LIST_ADAWAY);
        CompletableFuture<Set<AdHost>> task3 = fetchListFromUrl(AD_HOSTS_URLS_LIST_ADMIRAL);
        CompletableFuture<Set<AdHost>> task4 = fetchListFromUrl(TRACKING_BLACK_LIST);

        return CompletableFuture.allOf(task1, task2, task3, task4)
                .thenApply(v -> {
                    Set<AdHost> result = new HashSet<>();

                    // Handle each task and add results, ignoring failures
                    try {
                        result.addAll(task1.join());
                    } catch (CompletionException ignored) {
                        // Ignore failures
                    }

                    try {
                        result.addAll(task2.join());
                    } catch (CompletionException ignored) {
                        // Ignore failures
                    }

                    try {
                        result.addAll(task3.join());
                    } catch (CompletionException ignored) {
                        // Ignore failures
                    }

                    try {
                        result.addAll(task4.join());
                    } catch (CompletionException ignored) {
                        // Ignore failures
                    }

                    return result;
                });
    }

    private CompletableFuture<Set<AdHost>> fetchListFromUrl(String url) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder().url(url).build();

            try (Response response = okHttpClient.getProxyOkHttpClient().newCall(request).execute()) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        try (InputStream stream = body.byteStream()) {
                            return readAdServersFromStream(stream);
                        }
                    }
                }
            } catch (IOException e) {
                // Return empty set on error
            }

            return new HashSet<>();
        });
    }

    @Override
    public boolean isAds(String url) {
        throw new UnsupportedOperationException("use isAds from local");
    }

    @Override
    public CompletableFuture<Void> addHosts(Set<AdHost> hosts) {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("To remote hosts forbidden to add")
        );
    }

    @Override
    public CompletableFuture<Void> removeHosts(Set<AdHost> hosts) {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("To remote hosts forbidden to remove")
        );
    }

    @Override
    public CompletableFuture<Void> addHost(AdHost host) {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("To remote host forbidden to add")
        );
    }

    @Override
    public CompletableFuture<Void> removeHost(AdHost host) {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("To remote host forbidden to remove")
        );
    }

    @Override
    public CompletableFuture<Void> removeAllHost() {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("To remote host forbidden to removeAllHost")
        );
    }

    @Override
    public CompletableFuture<Integer> getHostsCount() {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("To remote host forbidden to getHostsCount")
        );
    }

    @Override
    public int getCachedCount() {
        return 0;
    }

    @Override
    public CompletableFuture<Boolean> initialize(boolean isUpdate) {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("no need to Initialize AdBlockHostsRemoteDataSource")
        );
    }

    private Set<AdHost> readAdServersFromStream(InputStream inputStream) throws IOException {
        Set<AdHost> result = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    String parsedLine = AdBlockerHelper.parseAdsLine(line);
                    if (domainPattern.matcher(parsedLine).matches()) {
                        result.add(new AdHost(parsedLine));
                    }
                }
            }
        }

        return result;
    }
}