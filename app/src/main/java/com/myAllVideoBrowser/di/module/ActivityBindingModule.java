package com.myAllVideoBrowser.di.module;

import com.myAllVideoBrowser.di.ActivityScoped;
import com.myAllVideoBrowser.ui.main.home.MainActivity;
import com.myAllVideoBrowser.di.module.activity.MainModule;
import com.myAllVideoBrowser.ui.main.player.VideoPlayerActivity;
import com.myAllVideoBrowser.di.module.activity.VideoPlayerModule;
import com.myAllVideoBrowser.ui.main.splash.SplashActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    abstract SplashActivity bindSplashActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {MainModule.class})
    abstract MainActivity bindMainActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {VideoPlayerModule.class})
    abstract VideoPlayerActivity bindVideoPlayerActivity();
}
