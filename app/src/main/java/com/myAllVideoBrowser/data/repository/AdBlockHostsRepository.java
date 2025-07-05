package com.myAllVideoBrowser.data.repository;

import com.myAllVideoBrowser.data.local.room.entity.AdHost;
import com.myAllVideoBrowser.di.qualifier.LocalData;
import com.myAllVideoBrowser.di.qualifier.RemoteData;
import com.myAllVideoBrowser.util.SharedPrefHelper;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

public interface AdBlockHostsRepository {
    CompletableFuture<Boolean> initialize(boolean isUpdate);

    CompletableFuture<Set<AdHost>> fetchHosts();

    boolean isAds(String url);

    CompletableFuture<Void> addHosts(Set<AdHost> hosts);

    CompletableFuture<Void> removeHosts(Set<AdHost> hosts);

    CompletableFuture<Void> addHost(AdHost host);

    CompletableFuture<Void> removeHost(AdHost host);

    CompletableFuture<Void> removeAllHost();

    CompletableFuture<Integer> getHostsCount();

    int getCachedCount();
}

