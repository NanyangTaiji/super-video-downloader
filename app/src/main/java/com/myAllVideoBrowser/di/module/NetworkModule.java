package com.myAllVideoBrowser.di.module;

import android.app.Application;
import com.myAllVideoBrowser.data.remote.service.ConfigService;
import com.myAllVideoBrowser.data.remote.service.VideoService;
import com.myAllVideoBrowser.data.remote.service.VideoServiceLocal;
import com.myAllVideoBrowser.util.Memory;
import com.myAllVideoBrowser.util.proxy_utils.CustomProxyController;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.File;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;

@Module
public class NetworkModule {

    private static final String DATA_URL = "https://some-url.com/youtube-dl/";

    private OkHttpClient buildOkHttpClient(Application application) {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10L, TimeUnit.SECONDS)
                .writeTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .cache(new Cache(
                        new File(application.getCacheDir(), "YoutubeDLCache"),
                        Memory.calcCacheSize(application, 0.25f)
                ))
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Application application) {
        return buildOkHttpClient(application);
    }

    @Provides
    @Singleton
    public ConfigService provideConfigService(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(DATA_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(ConfigService.class);
    }

    @Provides
    @Singleton
    public VideoService provideVideoService(CustomProxyController proxyController) {
        return new VideoServiceLocal(proxyController);
    }
}
