package com.example.argumentmapper.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.argumentmapper.R;

public class ShareMapDialogFragment extends DialogFragment {

    @Override
    public void onResume() {
        super.onResume();
        String link = ((ShareMapDialogProvider)getActivity()).getCode();
        ((TextView)this.getView().findViewById(R.id.textViewLink)).setText(link);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_share_map,null);
        (view.findViewById(R.id.btnClose)).setOnClickListener(view1 -> {
            dismiss();
        });
        (view.findViewById(R.id.btnCopy)).setOnClickListener(view1 -> {
            ClipboardManager myClipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String text = ((TextView)view.findViewById(R.id.textViewLink)).getText().toString();

            ClipData myClip = ClipData.newPlainText("text", text);
            myClipboard.setPrimaryClip(myClip);

            Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        ((ShareMapDialogProvider)getActivity()).forgetCode();
        super.onDismiss(dialog);
    }

    public static String TAG = "ShareMapDialog";
}