package com.myAllVideoBrowser.di.module.activity;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import com.myAllVideoBrowser.di.FragmentScoped;
import com.myAllVideoBrowser.ui.main.player.VideoPlayerFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class VideoPlayerModule {

    @OptIn(markerClass = UnstableApi.class)
    @FragmentScoped
    @ContributesAndroidInjector
    abstract VideoPlayerFragment bindVideoPlayerFragment();
}

