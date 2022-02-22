package com.example.voicerecording;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class StartSystemPage {
// Go to the current system setting page
    public static void goToSetting(Activity context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
