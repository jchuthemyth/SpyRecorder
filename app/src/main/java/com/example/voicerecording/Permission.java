package com.example.voicerecording;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {
    private Permission(){}
    private static Permission permission;
    private final int REQUEST_CODE = 100;
    public interface PermissionCallBackListener {
        void onGranted();                               //run this method if granted
        void onDenied(List<String> deniedPermissions);  //put all the denied permissions in parameter
    }
    private PermissionCallBackListener callBackListener;

    public static Permission getInstance() {
        if (permission == null) {
            synchronized (Permission.class) {
                if (permission == null) {
                    permission = new Permission();
                }
            }
        }
        return permission;
    }

    public void onRequestPermission(Activity context, String[] permissions
            , PermissionCallBackListener listener) {

        callBackListener = listener;

        // Decide if it's Android 6 and above
        if (Build.VERSION.SDK_INT >= 23) {
            //Make a list for all the permission that is not granted.
            List<String> mPermissionList = new ArrayList<>();
            //Check if each permission granted
            for(String p : permissions) {
                int result = ContextCompat.checkSelfPermission(context, p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(p);
                }
            }

            //Apply for permission for each element in mPermissionList above
            if (mPermissionList.size() > 0) {
                String[] permissionArray = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(context, permissionArray, REQUEST_CODE);
            }else {
                // if all the permissions are granted, do the following
                callBackListener.onGranted();
            }
        }
    }

    public void onRequestPermissionResult(Activity context, int requestCode
            , @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            List<String> deniedPermissions = new ArrayList<>();
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[i]);
                    }
                }
            }
            if (deniedPermissions.size() == 0) {
                callBackListener.onGranted();
            } else {
                callBackListener.onDenied(deniedPermissions);
            }
        } else {
            //All permissions received
            callBackListener.onGranted();
        }
    }

    public void promptSetting(Activity context) {
        PermissionDialog.showDialog(context, "Alert", "Please active it manually"
                , "Cancel", new PermissionDialog.LeftClickListener() {
                    @Override
                    public void onLeftClick() {
                        context.finish();
                    }
                }, "Confirm", new PermissionDialog.RightClickListener() {
                    @Override
                    public void onRightClick() {
                        StartSystemPage.goToSetting(context);
                        context.finish();
                    }
                });
    }
}

