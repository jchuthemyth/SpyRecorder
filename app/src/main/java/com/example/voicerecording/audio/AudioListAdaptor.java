package com.example.voicerecording.audio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.voicerecording.R;
import com.example.voicerecording.bean.ListBean;
import com.example.voicerecording.databinding.AudioCardBinding;

import java.util.List;

public class AudioListAdaptor extends BaseAdapter {
    private Context context;
    private List<ListBean>mData;

    //Interface that can be called everything the audioPlay(image button) is clicked.
    public interface OnAudioPlayClickListener {
        void onAudioPlayClick(AudioListAdaptor adaptor, View convertView, View playView, int position);
    }
    private OnAudioPlayClickListener onAudioPlayClickListener;

    public void setOnAudioPlayClickListener(OnAudioPlayClickListener onAudioPlayClickListener) {
        this.onAudioPlayClickListener = onAudioPlayClickListener;
    }


    public AudioListAdaptor(Context context, List<ListBean> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.audio_card, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //Get the position the data and make setting
        ListBean listBean = mData.get(i);
        viewHolder.cardBinding.audioTime.setText(listBean.getTime());
        viewHolder.cardBinding.audioDuration.setText(listBean.getDuration());
        viewHolder.cardBinding.audioTitle.setText(listBean.getTitle());
        if (listBean.isPlaying()) { //if it's in the state of playing audio
            viewHolder.cardBinding.audioPlay.setImageResource(R.drawable.pause);
        }else {
            viewHolder.cardBinding.audioPlay.setImageResource(R.drawable.play);
        }

        View convertView = view;
        //Make play button click-able to play audio
        viewHolder.cardBinding.audioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAudioPlayClickListener != null) {
                    onAudioPlayClickListener.onAudioPlayClick(AudioListAdaptor.this
                            , convertView, view, i);
                }
            }
        });


        return view;
    }

    class ViewHolder {
        AudioCardBinding cardBinding;
        public ViewHolder(View view) {
            cardBinding = AudioCardBinding.bind(view);
        }
    }
}
