package com.myAllVideoBrowser.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.myAllVideoBrowser.data.repository.AdBlockHostsRepository;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AdsInitializerHelper {

    private static final int ADS_LIST_UPDATE_TIME_DAYS = 7;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void initializeAdBlocker(
            AdBlockHostsRepository adBlockHostsRepository,
            SharedPrefHelper sharedPrefHelper
    ) {
        executorService.execute(() -> {
            if (!sharedPrefHelper.getIsAdBlocker()) return;

            if (adBlockHostsRepository.getCachedCount() > 0) return;

            boolean isOutdated = isAdHostsOutdated(sharedPrefHelper);

            ResultCallback callback = result -> {
                mainHandler.post(() -> {
                    showInitializationResultToast(result.isInitialized, result.isUpdated);
                });
            };

            if (isOutdated) {
                updateAdHosts(adBlockHostsRepository, sharedPrefHelper, callback);
            } else {
                initializeAdHosts(adBlockHostsRepository, callback);
            }
        });
    }

    private static boolean isAdHostsOutdated(SharedPrefHelper sharedPrefHelper) {
        long lastUpdateTime = sharedPrefHelper.getAdHostsUpdateTime();
        long currentTime = System.currentTimeMillis();
        long differenceInMillis = currentTime - lastUpdateTime;
        long daysDifference = TimeUnit.MILLISECONDS.toDays(differenceInMillis);
        return daysDifference > ADS_LIST_UPDATE_TIME_DAYS;
    }

    private static void updateAdHosts(
            AdBlockHostsRepository adBlockHostsRepository,
            SharedPrefHelper sharedPrefHelper,
            ResultCallback callback
    ) {
        AppLogger.d("HOST LIST OUTDATED, UPDATING...");
        mainHandler.post(() -> {
            Toast.makeText(
                    ContextUtils.getApplicationContext(),
                    "AdBlock hosts lists start updating...",
                    Toast.LENGTH_LONG
            ).show();
        });

        adBlockHostsRepository.initialize(true).thenAccept(isInitialized -> {
            if (isInitialized) {
                sharedPrefHelper.setIsAdHostsUpdateTime(new Date().getTime());
                AppLogger.d("HOST LISTS UPDATED DONE, TIME: " + new Date());
                callback.onResult(new InitializationResult(true, true));
            } else {
                AppLogger.d("HOST LISTS UPDATED FAIL, TIME: " + new Date());
                callback.onResult(new InitializationResult(false, false));
            }
        }).exceptionally(throwable -> {
            AppLogger.d("HOST LISTS UPDATED FAIL, TIME: " + new Date());
            callback.onResult(new InitializationResult(false, false));
            return null;
        });
    }

    private static void initializeAdHosts(AdBlockHostsRepository adBlockHostsRepository, ResultCallback callback) {
        adBlockHostsRepository.initialize(false).thenAccept(isInitialized -> {
            AppLogger.d("HOST LISTS INITIALIZED DONE, TIME: " + new Date());
            callback.onResult(new InitializationResult(isInitialized, false));
        }).exceptionally(throwable -> {
            AppLogger.d("HOST LISTS INITIALIZED FAIL, TIME: " + new Date());
            callback.onResult(new InitializationResult(false, false));
            return null;
        });
    }

    private static void showInitializationResultToast(boolean isInitialized, boolean isUpdated) {
        String message;
        if (isInitialized && !isUpdated) {
            message = "AdBlock hosts lists initialized";
        } else if (!isInitialized) {
            message = "AdBlock hosts lists initialized failed";
        } else {
            message = "AdBlock hosts lists initialized and updated";
        }

        Toast.makeText(
                ContextUtils.getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    // Helper class to replace Kotlin's Pair
    private static class InitializationResult {
        final boolean isInitialized;
        final boolean isUpdated;

        InitializationResult(boolean isInitialized, boolean isUpdated) {
            this.isInitialized = isInitialized;
            this.isUpdated = isUpdated;
        }
    }

    // Callback interface for async operations
    private interface ResultCallback {
        void onResult(InitializationResult result);
    }
}