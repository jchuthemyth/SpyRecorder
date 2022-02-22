package com.example.voicerecording.recorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.voicerecording.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecorderService extends Service {
    private MediaRecorder recorder;
    private boolean isRecording = false;
    private String inputDirectoryPath;
    private SimpleDateFormat fileNameFormat, timeFormat;
    private int time;

    public RecorderService() {}

    public class RecorderBinder extends Binder {
        public RecorderService getRecorderService() {
            return RecorderService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        inputDirectoryPath = Constants.FILE_DIRECTORY_PATH;
        fileNameFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
    }

    public interface OnRefreshRecordingTimeListener {
        void onRefresh(String time);
    }

    private OnRefreshRecordingTimeListener onRefreshRecordingTimeListener;

    public void setOnRefreshRecordingTimeListener(OnRefreshRecordingTimeListener onRefreshRecordingTimeListener) {
        this.onRefreshRecordingTimeListener = onRefreshRecordingTimeListener;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (recorder == null) {
                return false;
            }
            time += 1000;
            if (onRefreshRecordingTimeListener != null) {
                String timeStr = convertTime(time);
                onRefreshRecordingTimeListener.onRefresh(timeStr);
            }
            return false;
        }
    });

    private String convertTime(int millisecond) {
        millisecond -= 18 * 60 * 60 * 1000;
        return timeFormat.format(new Date(millisecond));
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            // use the while loop here, when isRecording turns false, it will stop the thread.
            while (isRecording) {
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    //Start Recording
    public void startRecording() {
        recorder = new MediaRecorder();
        isRecording = true;
        recorder.reset();
        setRecorder();
        try {
            recorder.prepare();
            recorder.start();
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Stop recording
    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder = null;
            time = 0;
            isRecording = false; // set to false to stop the thread.
        }
    }

    //Setting recorder
    private void setRecorder() {
        //setting the source of recording, which is the phone's microphone.
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        //setting the recording output file
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);

        //setting audio encoder
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        //creating audio file
        String fileName = fileNameFormat.format(new Date());
        File file = new File(inputDirectoryPath, fileName+".amr");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        recorder.setOutputFile(file.getAbsolutePath());


        //setting recording time - I set it to 180 minutes
        recorder.setMaxDuration(180 * 60 * 1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new RecorderBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}