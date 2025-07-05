package com.myAllVideoBrowser.data.repository;

import com.myAllVideoBrowser.data.local.room.entity.AdHost;
import com.myAllVideoBrowser.di.qualifier.LocalData;
import com.myAllVideoBrowser.di.qualifier.RemoteData;
import com.myAllVideoBrowser.util.SharedPrefHelper;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdBlockHostsRepositoryImpl implements AdBlockHostsRepository {

    private final AdBlockHostsRepository localDataSource;
    private final AdBlockHostsRepository remoteDataSource;
    private final SharedPrefHelper sharedPrefHelper;

    @Inject
    public AdBlockHostsRepositoryImpl(
            @LocalData AdBlockHostsRepository localDataSource,
            @RemoteData AdBlockHostsRepository remoteDataSource,
            SharedPrefHelper sharedPrefHelper
    ) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.sharedPrefHelper = sharedPrefHelper;
    }

    @Override
    public CompletableFuture<Boolean> initialize(boolean isUpdate) {
        boolean isPopulated = sharedPrefHelper.getIsPopulated();

        if (isUpdate) {
            return fetchHosts().thenCompose(freshHosts -> {
                if (!freshHosts.isEmpty()) {
                    return localDataSource.addHosts(freshHosts)
                            .thenApply(v -> true);
                }

                boolean remoteInitialized = !freshHosts.isEmpty();

                if (!isPopulated && !remoteInitialized) {
                    return localDataSource.initialize(true)
                            .thenApply(v -> false);
                }

                return CompletableFuture.completedFuture(remoteInitialized);
            });
        } else {
            return localDataSource.initialize(false);
        }
    }

    @Override
    public CompletableFuture<Set<AdHost>> fetchHosts() {
        return remoteDataSource.fetchHosts();
    }

    @Override
    public boolean isAds(String url) {
        return localDataSource.isAds(url);
    }

    @Override
    public CompletableFuture<Void> addHosts(Set<AdHost> hosts) {
        return localDataSource.addHosts(hosts);
    }

    @Override
    public CompletableFuture<Void> removeHosts(Set<AdHost> hosts) {
        return localDataSource.removeHosts(hosts);
    }

    @Override
    public CompletableFuture<Void> addHost(AdHost host) {
        return localDataSource.addHost(host);
    }

    @Override
    public CompletableFuture<Void> removeHost(AdHost host) {
        return localDataSource.removeHost(host);
    }

    @Override
    public CompletableFuture<Void> removeAllHost() {
        return localDataSource.removeAllHost();
    }

    @Override
    public CompletableFuture<Integer> getHostsCount() {
        return localDataSource.getHostsCount();
    }

    @Override
    public int getCachedCount() {
        return localDataSource.getCachedCount();
    }
}

