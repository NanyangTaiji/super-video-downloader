package com.myAllVideoBrowser.di.module;

import android.app.Application;
import android.content.Context;
import com.myAllVideoBrowser.DLApplication;
import com.myAllVideoBrowser.di.qualifier.ApplicationContext;
import com.myAllVideoBrowser.util.downloaders.NotificationReceiver;
import com.myAllVideoBrowser.util.scheduler.BaseSchedulers;
import com.myAllVideoBrowser.util.scheduler.BaseSchedulersImpl;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import javax.inject.Singleton;

@Module
public abstract class AppModule {

    @Binds
    @ApplicationContext
    abstract Context bindApplicationContext(DLApplication application);

    @Binds
    abstract Application bindApplication(DLApplication application);

    @Singleton
    @Binds
    abstract BaseSchedulers bindBaseSchedulers(BaseSchedulersImpl baseSchedulers);

    @ContributesAndroidInjector
    abstract NotificationReceiver contributesNotificationReceiver();
}

