package com.example.voicerecording;

import android.media.MediaMetadataRetriever;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioInfo {

    private MediaMetadataRetriever mediaMetadataRetriever; //data type to get media related info

    private AudioInfo(){}
    private static AudioInfo audioInfo;

    public static AudioInfo getInstance() {
        if (audioInfo == null) {
            synchronized (AudioInfo.class) {
                if (audioInfo == null) {
                    audioInfo = new AudioInfo();
                }
            }
        }
        return audioInfo;
    }

    public long getAudioFileDuration(String filePath) {
        if (mediaMetadataRetriever == null) {
            mediaMetadataRetriever = new MediaMetadataRetriever();
        }

        mediaMetadataRetriever.setDataSource(filePath);
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(durationStr);
    }

    public String getAudioFileDurationFormat(String format, long duration) {
        duration -= 18 * 3600 * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date(duration));
    }
    // Format duration to HH:mm:ss through the same method above
    public String getAudioFileDurationFormat(long duration) {
        return getAudioFileDurationFormat("HH:mm:ss", duration);
    }

    public void releaseMediaData() {
        if (mediaMetadataRetriever != null) {
            mediaMetadataRetriever.release();
            mediaMetadataRetriever = null;
        }
    }
}
