package com.example.voicerecording.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import com.example.voicerecording.AudioInfo;
import com.example.voicerecording.Constants;
import com.example.voicerecording.DetailsDialog;
import com.example.voicerecording.PermissionDialog;
import com.example.voicerecording.R;
import com.example.voicerecording.RenameDialog;
import com.example.voicerecording.bean.ListBean;
import com.example.voicerecording.databinding.ActivityAudioListBinding;
import com.example.voicerecording.recorder.RecorderActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AudioList extends AppCompatActivity {
    private ActivityAudioListBinding binding;
    private List<ListBean> mData;
    private AudioListAdaptor adaptor;
    private AudioService audioService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AudioService.AudioBinder audioBinder = (AudioService.AudioBinder) iBinder;
            audioService = audioBinder.getService();
            audioService.setOnPlayChangeListener(playChangeListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    AudioService.OnPlayChangeListener playChangeListener = new AudioService.OnPlayChangeListener() {
        @Override
        public void playChange(int changePosition) {
            adaptor.notifyDataSetChanged();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudioListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Binding AudioService
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        //Set ListView data and adaptor
        mData = new ArrayList<>();
        adaptor = new AudioListAdaptor(this, mData);
        binding.audioList.setAdapter(adaptor);

        // put the ListBean object(mData) into the Constant class myAudioList for service access.
        Constants.setMyAudioList(mData);

        //Loading data
        loadData();

        //Set event
        setEvents();
    }

    // Method to unbind the AudioService connection
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    public void setEvents() {
        adaptor.setOnAudioPlayClickListener(playClickListener);
        binding.audioList.setOnItemLongClickListener(longClickListener);
        binding.recordButton.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Once recorder button is clicked
            // stop the music first
            audioService.stopMusic();
            // create a new Intent, from this activity(AudioList) to RecorderActivity
            startActivity(new Intent(AudioList.this, RecorderActivity.class));
            // destroy this activity
            finish();
        }
    };

    AudioListAdaptor.OnAudioPlayClickListener playClickListener = new AudioListAdaptor.OnAudioPlayClickListener() {
        @Override
        public void onAudioPlayClick(AudioListAdaptor adaptor, View convertView, View playView, int position) {
            for (int i = 0; i < mData.size(); i++) { // loop through the ListBean and set all the isPlaying to false
                if (i == position) continue;         // except the position (parameter in this method) bean.
                ListBean listBean = mData.get(i);
                listBean.setPlaying(false);
            }
            boolean playing = mData.get(position).isPlaying();
            mData.get(position).setPlaying(!playing);
            adaptor.notifyDataSetChanged();
            audioService.playOrPause(position); //pass the position to the playOrPause method in AudioService class
        }
    };

    AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            showPopUpMenu(view, i);
            audioService.stopMusic();
            return false;
        }
    };

    //Long click every single item in the list will pop up a menu
    private void showPopUpMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.END);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.clickaudio_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_details:
                        showDetails(position);
                        break;
                    case R.id.menu_rename:
                        renameDialog(position);
                        break;
                    case R.id.menu_delete:
                        deleteFileByPosition(position);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    // Get details for the file in mData list that indicated by the position
    private void showDetails(int position) {
        ListBean listBean = mData.get(position);
        DetailsDialog detailsDialog = new DetailsDialog(this);
        detailsDialog.show();
        detailsDialog.setDetails(listBean);
        detailsDialog.setCanceledOnTouchOutside(false);

    }

    //Display rename dialog menu
    private void renameDialog(int position) {
        ListBean listBean = mData.get(position);
        String title = listBean.getTitle();
        RenameDialog renameDialog = new RenameDialog(this);
        renameDialog.show();
        renameDialog.setHintText(title);
        renameDialog.setOnConfirmListener(new RenameDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String message) {
                renameByPosition(message, position);
            }
        });
    }

    //Change the file name of this position with this message
    private void renameByPosition(String newTitle, int position) {
        ListBean listBean = mData.get(position);
        if (listBean.getTitle().equals(newTitle)) {
            return;
        }

        listBean.setTitle(newTitle);
        //listBean.setPath(newFilePath);
        adaptor.notifyDataSetChanged();
    }

    //Delete file by position in the parameter
    private void deleteFileByPosition(int position) {
        ListBean listBean = mData.get(position);
        String path = listBean.getPath();
        PermissionDialog.showDialog(this, "Alert!!"
                , "Do you want to delete this audio file", "Cancel"
                , null, "Delete", new PermissionDialog.RightClickListener() {
                    @Override
                    public void onRightClick() {
                        File file = new File(path);
                        file.getAbsoluteFile().delete();
                        mData.remove(listBean);
                        adaptor.notifyDataSetChanged();
                    }
                });
    }

    private void loadData() {
        //Loading Audio file from the folder
        File fileDir = new File(Constants.FILE_DIRECTORY_PATH);
        File[] listFile = fileDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if (new File(file,s).isDirectory()) {
                    return false;
                }
                return s.endsWith(".mp3") || s.endsWith(".amr") || s.endsWith(".wav");
            }
        });

        //Define a date format for the file date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        AudioInfo audioInfo = AudioInfo.getInstance();

        //Loop through all the file from listFile array generated above, to obtains file information
        if (listFile != null) {
            int index = 0;
            for (File file : listFile) {
                String id = String.valueOf(index);
                index++;
                String fileName = file.getName(); //get name of each file in the array
                String title = fileName.substring(0,fileName.lastIndexOf("."));
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                long lastModified = file.lastModified();
                String fileDate = dateFormat.format(lastModified);
                long fileLength = file.length();
                String filePath = file.getAbsolutePath();
                long duration = audioInfo.getAudioFileDuration(filePath);
                String durationFormat = audioInfo.getAudioFileDurationFormat(duration);

                ListBean listBean = new ListBean(id, title, fileDate, durationFormat, filePath
                        , duration, lastModified, suffix, fileLength);
                mData.add(listBean);
            }
        }

        audioInfo.releaseMediaData(); //Release MediaMetadataRetriever object;

        // Sort mData list from the latest to the oldest, Using Collections.sort method.
        Collections.sort(mData, new Comparator<ListBean>() {
            @Override
            public int compare(ListBean l1, ListBean l2) {
                if (l1.getLastModified() < l2.getLastModified()) {
                    return 1;
                } else if (l1.getLastModified() == l2.getLastModified()) {
                    return 0;
                }
                return -1;
            }
        });

        //make change to AudioListAdaptor
        adaptor.notifyDataSetChanged();
    }
}