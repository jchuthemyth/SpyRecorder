package com.example.voicerecording;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voicerecording.audio.AudioList;
import com.example.voicerecording.databinding.ActivityMainBinding;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int countdown = 3;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.RECORD_AUDIO};
    private ActivityMainBinding binding;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 1) {
                countdown--;
                if (countdown == 0) {
                    startActivity(new Intent(MainActivity.this, AudioList.class));
                    finish();
                }else {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Permission.getInstance().onRequestPermission(this, permissions, listener);
    }

    Permission.PermissionCallBackListener listener = new Permission.PermissionCallBackListener() {
        @Override
        public void onGranted() {
            //Check if the directory is available, if not, create one.
            createAppDirectory();

            //Delay a bit to display app entrance animation
            handler.sendEmptyMessageDelayed(1, 1000);
        }

        @Override
        public void onDenied(List<String> deniedPermissions) {
            Permission.getInstance().promptSetting(MainActivity.this);
        }
    };

    private void createAppDirectory() {
        File file = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC)));
        Constants.FILE_DIRECTORY_PATH = file.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permission.getInstance().onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }
}