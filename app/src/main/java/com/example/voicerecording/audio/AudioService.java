package com.example.voicerecording.audio;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.voicerecording.Constants;
import com.example.voicerecording.bean.ListBean;

import java.util.List;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer = null; // Initial MediaPlayer
    private List<ListBean> pList; //Play list declaration
    private int currentPosition = -1; // Current playing position

    public AudioService() {}

    public interface OnPlayChangeListener {
        void playChange(int changePosition);
    }
    private OnPlayChangeListener onPlayChangeListener;

    public void setOnPlayChangeListener(OnPlayChangeListener onPlayChangeListener) {
        this.onPlayChangeListener = onPlayChangeListener;
    }

    // Notify Activity to refresh the button
    public void notifyActivityRefreshButton() {
        if (onPlayChangeListener != null) {
            onPlayChangeListener.playChange(currentPosition);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextMusic();
    }

    // Will play the next audio file in list upon completion of the current audio file
    private void nextMusic() {
        pList.get(currentPosition).setPlaying(false);
        if (currentPosition >= pList.size() - 1) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        pList.get(currentPosition).setPlaying(true);
        play(currentPosition);
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            pList.get(currentPosition).setPlaying(false);
            notifyActivityRefreshButton();
            mediaPlayer.stop();
            currentPosition = -1;
        }
    }


    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AudioBinder();
    }

    // If the current position button is clicked, it's pause or continue
    // If not the current position button is clicked, then play the audio in that position
    public void playOrPause(int position) {
        int currentPosition = this.currentPosition;
        if (position != currentPosition) {
            if (currentPosition != -1) { //because we initialize currentPosition to -1
                pList.get(currentPosition).setPlaying(false);
            }
            play(position);
            return;
        }
        pause();
    }

    //Playing audio file
    public void play(int position) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            // set a listener here
            mediaPlayer.setOnCompletionListener(this);
        }
        // Start the process of playing the audio file
        pList = Constants.getMyAudioList(); //get the play list from Constant class
        if (pList.size() <= 0) {        //make sure the play list is not empty
            return;
        }

        if (mediaPlayer.isPlaying()) { //stop the media player if it's currently playing other file
            mediaPlayer.stop();
        }

        try {
            mediaPlayer.reset(); //reset and release resources before changing Audio file
            currentPosition = position; //update the current playing position
            //note that setDataSource method below will throw Exception, so try catch is used.
            mediaPlayer.setDataSource(pList.get(position).getPath()); //set the audio file path to the media player
            mediaPlayer.prepare();
            mediaPlayer.start();
            pList.get(position).setPlaying(true); //set the isPlaying boolean in ListBean to true
            notifyActivityRefreshButton(); //change button
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // PAUSE playing the audio file
    public void pause() {
        int currentPosition = this.currentPosition;
        ListBean listBean = pList.get(currentPosition);
        if (mediaPlayer.isPlaying()) {  // if the music is playing, pause and update the setPlaying
            mediaPlayer.pause();
            listBean.setPlaying(false);
        } else {                        // else,
            mediaPlayer.start();
            listBean.setPlaying(true);
        }
        notifyActivityRefreshButton();

    }
}