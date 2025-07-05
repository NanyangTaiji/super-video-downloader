package com.myAllVideoBrowser.di.module;

import com.myAllVideoBrowser.data.local.AdBlockHostsLocalDataSource;
import com.myAllVideoBrowser.data.local.ConfigLocalDataSource;
import com.myAllVideoBrowser.data.local.HistoryLocalDataSource;
import com.myAllVideoBrowser.data.local.ProgressLocalDataSource;
import com.myAllVideoBrowser.data.local.TopPagesLocalDataSource;
import com.myAllVideoBrowser.data.local.VideoLocalDataSource;
import com.myAllVideoBrowser.data.remote.AdBlockHostsRemoteDataSource;
import com.myAllVideoBrowser.data.remote.ConfigRemoteDataSource;
import com.myAllVideoBrowser.data.remote.TopPagesRemoteDataSource;
import com.myAllVideoBrowser.data.remote.VideoRemoteDataSource;
import com.myAllVideoBrowser.data.repository.AdBlockHostsRepository;
import com.myAllVideoBrowser.data.repository.AdBlockHostsRepositoryImpl;
import com.myAllVideoBrowser.data.repository.ConfigRepository;
import com.myAllVideoBrowser.data.repository.ConfigRepositoryImpl;
import com.myAllVideoBrowser.data.repository.HistoryRepository;
import com.myAllVideoBrowser.data.repository.HistoryRepositoryImpl;
import com.myAllVideoBrowser.data.repository.ProgressRepository;
import com.myAllVideoBrowser.data.repository.ProgressRepositoryImpl;
import com.myAllVideoBrowser.data.repository.TopPagesRepository;
import com.myAllVideoBrowser.data.repository.TopPagesRepositoryImpl;
import com.myAllVideoBrowser.data.repository.VideoRepository;
import com.myAllVideoBrowser.data.repository.VideoRepositoryImpl;
import com.myAllVideoBrowser.di.qualifier.LocalData;
import com.myAllVideoBrowser.di.qualifier.RemoteData;
import dagger.Binds;
import dagger.Module;
import javax.inject.Singleton;

@Module
public abstract class RepositoryModule {

    @Singleton
    @Binds
    @LocalData
    public abstract ConfigRepository bindConfigLocalDataSource(ConfigLocalDataSource localDataSource);

    @Singleton
    @Binds
    @RemoteData
    public abstract ConfigRepository bindConfigRemoteDataSource(ConfigRemoteDataSource remoteDataSource);

    @Singleton
    @Binds
    public abstract ConfigRepository bindConfigRepositoryImpl(ConfigRepositoryImpl configRepository);

    @Singleton
    @Binds
    @LocalData
    public abstract TopPagesRepository bindTopPagesLocalDataSource(TopPagesLocalDataSource localDataSource);

    @Singleton
    @Binds
    @RemoteData
    public abstract TopPagesRepository bindTopPagesRemoteDataSource(TopPagesRemoteDataSource remoteDataSource);

    @Singleton
    @Binds
    public abstract TopPagesRepository bindTopPagesRepositoryImpl(TopPagesRepositoryImpl topPagesRepository);

    @Singleton
    @Binds
    @LocalData
    public abstract VideoRepository bindVideoLocalDataSource(VideoLocalDataSource localDataSource);

    @Singleton
    @Binds
    @RemoteData
    public abstract VideoRepository bindVideoRemoteDataSource(VideoRemoteDataSource remoteDataSource);

    @Singleton
    @Binds
    public abstract VideoRepository bindVideoRepositoryImpl(VideoRepositoryImpl videoRepository);

    @Singleton
    @Binds
    @LocalData
    public abstract ProgressRepository bindProgressLocalDataSource(ProgressLocalDataSource localDataSource);

    @Singleton
    @Binds
    @LocalData
    public abstract HistoryRepository bindHistoryLocalDataSource(HistoryLocalDataSource localDataSource);

    @Singleton
    @Binds
    public abstract ProgressRepository bindProgressRepositoryImpl(ProgressRepositoryImpl progressRepository);

    @Singleton
    @Binds
    public abstract HistoryRepository bindHistoryRepositoryImpl(HistoryRepositoryImpl historyRepository);

    @Singleton
    @Binds
    @LocalData
    public abstract AdBlockHostsRepository bindAdBlockHostsLocalDataSource(AdBlockHostsLocalDataSource adBlockHostsLocalDataSource);

    @Singleton
    @Binds
    @RemoteData
    public abstract AdBlockHostsRepository bindAdBlockHostsRemoteDataSource(AdBlockHostsRemoteDataSource adBlockHostsRemoteDataSource);

    @Singleton
    @Binds
    public abstract AdBlockHostsRepository bindAdBlockHostsRepositoryImpl(AdBlockHostsRepositoryImpl adBlockHostsRepository);
}

