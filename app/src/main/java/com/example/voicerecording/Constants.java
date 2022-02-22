package com.example.voicerecording;

import com.example.voicerecording.bean.ListBean;

import java.util.List;

public class Constants {

    public static String APP_DIRECTORY_PATH;
    public static String FILE_DIRECTORY_PATH;

    private static List<ListBean> myAudioList;

    public static void setMyAudioList(List<ListBean> myAudioList) {
        if (myAudioList != null) {
            Constants.myAudioList = myAudioList;
        }
    }

    public static List<ListBean> getMyAudioList() {
        return myAudioList;
    }
}
