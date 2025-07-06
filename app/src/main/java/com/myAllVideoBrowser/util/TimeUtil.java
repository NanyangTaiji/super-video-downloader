package com.myAllVideoBrowser.util;

public class TimeUtil {

    public static String convertMilliSecondsToTimer(long milliSeconds) {
        String hourString;
        String secondString;
        String minuteString;

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) ((milliSeconds % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60) % (1000 * 60) / 1000));

        hourString = hours > 0 ? hours + ":" : "";
        minuteString = minutes < 10 ? "0" + minutes : "" + minutes;
        secondString = seconds < 10 ? "0" + seconds : "" + seconds;

        return hourString + minuteString + ":" + secondString;
    }
}
