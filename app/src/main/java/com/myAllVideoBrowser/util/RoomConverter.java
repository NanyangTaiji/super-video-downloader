package com.myAllVideoBrowser.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.myAllVideoBrowser.data.local.room.entity.VideoInfo;

public class RoomConverter {

    @TypeConverter
    public VideoInfo convertJsonToVideo(String json) {
        return new Gson().fromJson(json, VideoInfo.class);
    }

    @TypeConverter
    public String convertListVideosToJson(VideoInfo video) {
        return new Gson().toJson(video);
    }
}
