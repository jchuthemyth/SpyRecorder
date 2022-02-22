package com.example.voicerecording;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.voicerecording.databinding.RenameDialogBinding;

public class RenameDialog extends Dialog implements View.OnClickListener {
    private RenameDialogBinding binding;

    //Interface for rename confirm button click
    public interface OnConfirmListener {
        void onConfirm(String message); //message is the new name of file
    }

    private OnConfirmListener onConfirmListener;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public RenameDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RenameDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.renameButtonConfirm.setOnClickListener(this);
        binding.renameButtonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rename_button_confirm:
                if (onConfirmListener != null) {
                    String message = binding.renameDialogEditText.getText().toString().trim();
                    onConfirmListener.onConfirm(message);
                }
                cancel();
                break;
            case R.id.rename_button_cancel:
                cancel();
                break;
        }
    }

    //Put the original file name as hint text
    public void setHintText(String previousFileName) {
        binding.renameDialogEditText.setText(previousFileName);
    }
}
