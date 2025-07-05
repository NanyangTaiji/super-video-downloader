package com.myAllVideoBrowser.data.local.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.myAllVideoBrowser.data.local.room.entity.VideoInfo;

public class VideoInfoWrapper {
    @SerializedName("info")
    @Expose
    private VideoInfo videoInfo;

    public VideoInfoWrapper() {}

    public VideoInfoWrapper(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }
}
