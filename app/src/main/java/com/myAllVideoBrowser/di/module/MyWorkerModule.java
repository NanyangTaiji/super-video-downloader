package com.myAllVideoBrowser.di.module;

import androidx.work.WorkerFactory;
import com.myAllVideoBrowser.data.repository.ProgressRepository;
import com.myAllVideoBrowser.util.FileUtil;
import com.myAllVideoBrowser.util.NotificationsHelper;
import com.myAllVideoBrowser.util.SharedPrefHelper;
import com.myAllVideoBrowser.util.downloaders.generic_downloader.DaggerWorkerFactory;
import com.myAllVideoBrowser.util.proxy_utils.CustomProxyController;
import com.myAllVideoBrowser.util.proxy_utils.OkHttpProxyClient;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class MyWorkerModule {

    @Provides
    @Singleton
    public WorkerFactory workerFactory(
            ProgressRepository progressRepository,
            FileUtil fileUtil,
            NotificationsHelper notificationsHelper,
            CustomProxyController proxyController,
            OkHttpProxyClient okHttpProxyClient,
            SharedPrefHelper sharedPrefHelper
    ) {
        return new DaggerWorkerFactory(
                progressRepository,
                fileUtil,
                notificationsHelper,
                proxyController,
                okHttpProxyClient,
                sharedPrefHelper
        );
    }
}