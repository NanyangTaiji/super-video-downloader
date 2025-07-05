package com.myAllVideoBrowser.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.myAllVideoBrowser.di.ViewModelKey;
import com.myAllVideoBrowser.ui.main.history.HistoryViewModel;
import com.myAllVideoBrowser.ui.main.home.browser.BrowserViewModel;
import com.myAllVideoBrowser.ui.main.home.MainViewModel;
import com.myAllVideoBrowser.ui.main.home.browser.homeTab.BrowserHomeViewModel;
import com.myAllVideoBrowser.ui.main.home.browser.detectedVideos.GlobalVideoDetectionModel;
import com.myAllVideoBrowser.ui.main.home.browser.webTab.WebTabViewModel;
import com.myAllVideoBrowser.ui.main.home.browser.detectedVideos.VideoDetectionTabViewModel;
import com.myAllVideoBrowser.ui.main.player.VideoPlayerViewModel;
import com.myAllVideoBrowser.ui.main.progress.ProgressViewModel;
import com.myAllVideoBrowser.ui.main.proxies.ProxiesViewModel;
import com.myAllVideoBrowser.ui.main.settings.SettingsViewModel;
import com.myAllVideoBrowser.ui.main.splash.SplashViewModel;
import com.myAllVideoBrowser.ui.main.video.VideoViewModel;
import com.myAllVideoBrowser.util.ViewModelFactory;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import javax.inject.Singleton;

@Module(includes = {AppModule.class})
public abstract class ViewModelModule {

    @Singleton
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel.class)
    abstract ViewModel bindSplashViewModel(SplashViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BrowserViewModel.class)
    abstract ViewModel bindBrowserViewModel(BrowserViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(VideoPlayerViewModel.class)
    abstract ViewModel bindVideoPlayerViewModel(VideoPlayerViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProgressViewModel.class)
    abstract ViewModel bindProgressViewModel(ProgressViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(VideoViewModel.class)
    abstract ViewModel bindVideoViewModel(VideoViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel.class)
    abstract ViewModel bindSettingsViewModel(SettingsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel.class)
    abstract ViewModel bindHistoryViewModel(HistoryViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProxiesViewModel.class)
    abstract ViewModel bindProxiesViewModel(ProxiesViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(WebTabViewModel.class)
    abstract ViewModel bindWebTabViewModel(WebTabViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BrowserHomeViewModel.class)
    abstract ViewModel bindBrowserHomeViewModel(BrowserHomeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GlobalVideoDetectionModel.class)
    abstract ViewModel bindVideoDetectionAlgViewModel(GlobalVideoDetectionModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(VideoDetectionTabViewModel.class)
    abstract ViewModel bindVideoDetectionDetectedViewModel(VideoDetectionTabViewModel viewModel);
}
