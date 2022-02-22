package com.example.voicerecording.recorder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.example.voicerecording.R;
import com.example.voicerecording.audio.AudioList;
import com.example.voicerecording.databinding.ActivityRecorderBinding;

import org.firezenk.audiowaves.Visualizer;

public class RecorderActivity extends AppCompatActivity {

    private ActivityRecorderBinding binding;
    private RecorderService recorderService;

    RecorderService.OnRefreshRecordingTimeListener onRefreshRecordingTimeListener =
            new RecorderService.OnRefreshRecordingTimeListener() {
                @Override
                public void onRefresh(String time) {
                    binding.recorderTime.setText(time);
                }
            };


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RecorderService.RecorderBinder binder = (RecorderService.RecorderBinder) iBinder;
            recorderService = binder.getRecorderService();
            recorderService.startRecording();
            recorderService.setOnRefreshRecordingTimeListener(onRefreshRecordingTimeListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecorderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = new Intent(this, RecorderService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        ((Visualizer) findViewById(R.id.recorder_wave)).startListening(); //to stop: .stopListening()
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        if (view.getId() == R.id.recorder_stop) {
            recorderService.stopRecording();
            ((Visualizer) findViewById(R.id.recorder_wave)).stopListening();
            Intent intent = new Intent(this, AudioList.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connection != null) {
            unbindService(connection);
        }
    }
}