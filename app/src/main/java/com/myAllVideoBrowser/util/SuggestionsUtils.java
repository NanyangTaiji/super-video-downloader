package com.myAllVideoBrowser.util;

import com.myAllVideoBrowser.data.local.model.Suggestion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SuggestionsUtils {

    public static Flowable<List<Suggestion>> getSuggestions(OkHttpClient okHttpClient, String input) {
        return Flowable.create(emitter -> {
            Request request = new Request.Builder()
                    .url("https://duckduckgo.com/ac/?q=" + input + "&kl=wt-wt")
                    .build();

            String response = okHttpClient.newCall(request).execute().body().string();
            List<Suggestion> result = new ArrayList<>();

            JSONArray jsn = new JSONArray(response);
            for (int i = 0; i < jsn.length(); i++) {
                try {
                    JSONObject phraseObj = new JSONObject(jsn.get(i).toString());
                    String phrase = phraseObj.get("phrase").toString();
                    result.add(new Suggestion(phrase));
                } catch (Throwable ignored) {
                    // Ignore parsing errors
                }
            }
            emitter.onNext(result);
            emitter.onComplete();
        }, BackpressureStrategy.LATEST);
    }
}
