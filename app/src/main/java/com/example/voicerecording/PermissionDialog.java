package com.example.voicerecording;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class PermissionDialog {

    public interface LeftClickListener {
        void onLeftClick();
    }

    public interface RightClickListener {
        void onRightClick();
    }

    public static void showDialog(Context context, String title, String message, String leftBtn
            , LeftClickListener leftClickListener, String rightBtn
            , RightClickListener rightClickListener ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message);

        builder.setNegativeButton(leftBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (leftClickListener != null) {
                    leftClickListener.onLeftClick();
                    dialogInterface.cancel();
                }
            }
        });

        builder.setPositiveButton(rightBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (rightClickListener != null) {
                    rightClickListener.onRightClick();
                    dialogInterface.cancel();
                }
            }
        });

        builder.create().show();
    }
}
