package com.myAllVideoBrowser.data.local;

import android.net.Uri;
import com.myAllVideoBrowser.R;
import com.myAllVideoBrowser.data.local.room.dao.AdHostDao;
import com.myAllVideoBrowser.data.local.room.entity.AdHost;
import com.myAllVideoBrowser.data.repository.AdBlockHostsRepository;
import com.myAllVideoBrowser.util.AdBlockerHelper;
import com.myAllVideoBrowser.util.ContextUtils;
import com.myAllVideoBrowser.util.SharedPrefHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdBlockHostsLocalDataSource implements AdBlockHostsRepository {

    private final AdHostDao adHostDao;
    private final SharedPrefHelper sharedPrefHelper;
    private final Set<AdHost> hostsCache = new HashSet<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Pattern domainPattern = Pattern.compile(".+\\..+");

    @Inject
    public AdBlockHostsLocalDataSource(
            AdHostDao adHostDao,
            SharedPrefHelper sharedPrefHelper
    ) {
        this.adHostDao = adHostDao;
        this.sharedPrefHelper = sharedPrefHelper;
    }

    @Override
    public CompletableFuture<Boolean> initialize(boolean isUpdate) {
        return fetchHosts().thenApply(hosts -> !hosts.isEmpty());
    }

    @Override
    public CompletableFuture<Set<AdHost>> fetchHosts() {
        return CompletableFuture.supplyAsync(() -> {
            boolean isPopulated = sharedPrefHelper.getIsPopulated();

            if (isPopulated) {
                List<AdHost> hosts = adHostDao.getAdHosts();
                hostsCache.addAll(hosts);
                return hostsCache;
            } else {
                return fetchHostsFromFiles();
            }
        }, executorService);
    }

    @Override
    public boolean isAds(String url) {
        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            if (host != null) {
                host = host.replace("www.", "")
                        .replace("m.", "")
                        .trim();
                if (!host.isEmpty()) {
                    return hostsCache.contains(new AdHost());
                }
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return false;
    }

    @Override
    public CompletableFuture<Void> addHosts(Set<AdHost> hosts) {
        return CompletableFuture.runAsync(() -> {
            adHostDao.insertAdHosts(hosts);
            hostsCache.addAll(hosts);
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> addHost(AdHost host) {
        return CompletableFuture.runAsync(() -> {
            adHostDao.insertAdHost(host);
            hostsCache.add(host);
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> removeHosts(Set<AdHost> hosts) {
        return CompletableFuture.runAsync(() -> {
            adHostDao.deleteAdHosts(hosts);
            hostsCache.removeAll(hosts);
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> removeHost(AdHost host) {
        return CompletableFuture.runAsync(() -> {
            adHostDao.deleteAdHost(host);
            hostsCache.remove(host);
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> removeAllHost() {
        return CompletableFuture.runAsync(() -> {
            adHostDao.deleteAllAdHosts();
            hostsCache.clear();
        }, executorService);
    }

    @Override
    public CompletableFuture<Integer> getHostsCount() {
        return CompletableFuture.supplyAsync(() -> adHostDao.getHostsCount(), executorService);
    }

    @Override
    public int getCachedCount() {
        return hostsCache.size();
    }

    private Set<AdHost> fetchHostsFromFiles() {
        Set<AdHost> allHosts = new HashSet<>();
        int counter = 0;

        try {
            // Process all three files
            Set<AdHost> hosts1 = fetchHostsFromFileRaw(R.raw.adblockserverlist);
            Set<AdHost> hosts2 = fetchHostsFromFileRaw(R.raw.adblockserverlist2);
            Set<AdHost> hosts3 = fetchHostsFromFileRaw(R.raw.adblockserverlist3);

            // Add hosts in batches to database
            if (!hosts1.isEmpty()) {
                adHostDao.insertAdHosts(hosts1);
                allHosts.addAll(hosts1);
                counter += hosts1.size();
            }

            if (!hosts2.isEmpty()) {
                adHostDao.insertAdHosts(hosts2);
                allHosts.addAll(hosts2);
                counter += hosts2.size();
            }

            if (!hosts3.isEmpty()) {
                adHostDao.insertAdHosts(hosts3);
                allHosts.addAll(hosts3);
                counter += hosts3.size();
            }

            // Mark as populated if we have enough hosts
            if (counter > 80000) {
                sharedPrefHelper.setIsPopulated(true);
            }

            // Update cache with all hosts from database
            hostsCache.addAll(adHostDao.getAdHosts());

        } catch (Exception e) {
            // Log error but don't throw
            e.printStackTrace();
        }

        return hostsCache;
    }

    private Set<AdHost> fetchHostsFromFileRaw(int resource) {
        try {
            InputStream inputStream = ContextUtils.getApplicationContext()
                    .getResources()
                    .openRawResource(resource);
            return readAdServersFromStream(inputStream);
        } catch (Exception e) {
            // Return empty set on error
            return new HashSet<>();
        }
    }

    private Set<AdHost> readAdServersFromStream(InputStream inputStream) {
        Set<AdHost> hosts = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            hosts = reader.lines()
                    .filter(line -> !line.startsWith("#"))
                    .map(AdBlockerHelper::parseAdsLine)
                    .filter(line -> domainPattern.matcher(line).matches())
                    .map(AdHost::new)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hosts;
    }
}