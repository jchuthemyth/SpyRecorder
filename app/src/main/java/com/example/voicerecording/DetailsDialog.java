package com.example.voicerecording;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.voicerecording.bean.ListBean;
import com.example.voicerecording.databinding.ItemDetailsBinding;

import java.text.DecimalFormat;

public class DetailsDialog extends Dialog {
    private ItemDetailsBinding binding;
    public DetailsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ItemDetailsBinding.inflate(getLayoutInflater());
        CardView root = binding.getRoot();
        setContentView(root);
        binding.detailsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    public void setDetails(ListBean listBean) {
        binding.detailsTitle.setText(listBean.getTitle());
        binding.detailsDate.setText(listBean.getTime());
        String fileSize = calculateFileSize(listBean.getFileLength());
        binding.detailsSize.setText(fileSize);
        binding.detailsPath.setText(listBean.getPath());
    }

    private String calculateFileSize(long fileLength) {
        int kb = 1024;
        int mb = kb * kb;
        DecimalFormat sizeFormat = new DecimalFormat("#.00");
        if (fileLength >= mb) {
            return sizeFormat.format(fileLength * 1.0 / mb) + "MB";
        }

        if (fileLength >= kb) {
            return sizeFormat.format(fileLength * 1.0 / kb) + "KB";
        }

        return fileLength + "B";
    }
}
