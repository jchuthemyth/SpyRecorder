package com.example.voicerecording;

import android.os.Environment;

import java.io.File;

// I wrote this class and figured out Android uses Scoped Storage now. So, this class is never used.
// I keep this as a reference of what I have learned while writing this class.

public class SDCard {

    private SDCard() {}

    private static SDCard sdcard;
    public static SDCard getInstance() {
        if (sdcard == null) {
            synchronized (SDCard.class) {
                if (sdcard == null) {
                    sdcard = new SDCard();
                }
            }
        }
        return sdcard;
    }

    //Check if there's SD card
    public boolean isCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    //Create file
    public File createPublicDirectory() {
        if (isCardAvailable()) {
            File sdDir = Environment.getExternalStorageDirectory();
            File appDir = new File(sdDir, FileInterface.APP_DIRECTORY);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            Constants.APP_DIRECTORY_PATH = appDir.getAbsolutePath();
            return appDir;
        }
        return null;
    }

    public File createFetchDirectory(String dir) {
        File publicDir = createPublicDirectory();
        if (publicDir != null) {
            File fetchDir = new File(publicDir, dir);
            if (!fetchDir.exists()) {
                fetchDir.mkdirs();
            }
            return fetchDir;
        }
        return null;
    }
}
