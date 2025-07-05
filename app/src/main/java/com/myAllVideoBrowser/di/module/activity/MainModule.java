package com.myAllVideoBrowser.di.module.activity;

import android.app.Activity;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import com.myAllVideoBrowser.di.ActivityScoped;
import com.myAllVideoBrowser.di.FragmentScoped;
import com.myAllVideoBrowser.ui.main.bookmarks.BookmarksFragment;
import com.myAllVideoBrowser.ui.main.help.HelpFragment;
import com.myAllVideoBrowser.ui.main.history.HistoryFragment;
import com.myAllVideoBrowser.ui.main.home.browser.BrowserFragment;
import com.myAllVideoBrowser.ui.main.home.MainActivity;
import com.myAllVideoBrowser.ui.main.home.browser.homeTab.BrowserHomeFragment;
import com.myAllVideoBrowser.ui.main.home.browser.webTab.WebTabFragment;
import com.myAllVideoBrowser.ui.main.home.browser.detectedVideos.DetectedVideosTabFragment;
import com.myAllVideoBrowser.ui.main.progress.ProgressFragment;
import com.myAllVideoBrowser.ui.main.proxies.ProxiesFragment;
import com.myAllVideoBrowser.ui.main.settings.SettingsFragment;
import com.myAllVideoBrowser.ui.main.video.VideoFragment;
import com.myAllVideoBrowser.util.fragment.FragmentFactory;
import com.myAllVideoBrowser.util.fragment.FragmentFactoryImpl;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainModule {

    @OptIn(markerClass = UnstableApi.class)
    @FragmentScoped
    @ContributesAndroidInjector
    abstract BrowserFragment bindBrowserFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ProxiesFragment bindProxiesFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract HistoryFragment bindHistoryFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract HelpFragment bindHelpFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ProgressFragment bindProgressFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract VideoFragment bindVideoFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract SettingsFragment bindSettingsFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract WebTabFragment bindWebTabFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract DetectedVideosTabFragment bindDetectedVideosFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract BrowserHomeFragment bindBrowserHomeFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract BookmarksFragment bindBookmarksFragment();

    @ActivityScoped
    @Binds
    abstract Activity bindMainActivity(MainActivity mainActivity);

    @ActivityScoped
    @Binds
    abstract FragmentFactory bindFragmentFactory(FragmentFactoryImpl fragmentFactory);
}
