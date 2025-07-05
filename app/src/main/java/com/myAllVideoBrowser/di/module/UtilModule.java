package com.myAllVideoBrowser.di.module;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import com.myAllVideoBrowser.DLApplication;
import com.myAllVideoBrowser.util.AppUtil;
import com.myAllVideoBrowser.util.FileUtil;
import com.myAllVideoBrowser.util.IntentUtil;
import com.myAllVideoBrowser.util.NotificationsHelper;
import com.myAllVideoBrowser.util.SharedPrefHelper;
import com.myAllVideoBrowser.util.SystemUtil;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class UtilModule {

    @Singleton
    @Provides
    public DownloadManager bindDownloadManager(Application application) {
        return (DownloadManager) application.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Singleton
    @Provides
    public FileUtil bindFileUtil() {
        return new FileUtil();
    }

    @Singleton
    @Provides
    public SystemUtil bindSystemUtil() {
        return new SystemUtil();
    }

    @Singleton
    @Provides
    public IntentUtil bindIntentUtil(FileUtil fileUtil) {
        return new IntentUtil(fileUtil);
    }

    @Singleton
    @Provides
    public AppUtil bindAppUtil() {
        return new AppUtil();
    }

    @Singleton
    @Provides
    public NotificationsHelper provideNotificationsHelper(DLApplication dlApplication) {
        return new NotificationsHelper(dlApplication.getApplicationContext());
    }

    @Singleton
    @Provides
    public SharedPrefHelper provideSharedPrefHelper(DLApplication dlApplication, AppUtil appUtil) {
        return new SharedPrefHelper(dlApplication.getApplicationContext(), appUtil);
    }
}
