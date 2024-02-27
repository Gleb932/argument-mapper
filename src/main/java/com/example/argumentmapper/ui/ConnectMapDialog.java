package com.example.argumentmapper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.argumentmapper.R;

public class ConnectMapDialog extends AppCompatDialogFragment {

    private EditText etID;
    private ConnectMapDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        listener = (ConnectMapDialogListener)activity;

        final View view = inflater.inflate(R.layout.dialog_connect_map, null);
        etID = view.findViewById(R.id.etID);

        builder.setView(view)
        .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(!dataIsValid())
                {
                    Toast.makeText(getActivity(), "ID is invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    listener.onFinishConnectMapDialogListener(Integer.parseInt(etID.getText().toString()));
                } catch (NumberFormatException e) {
                    return;
                }
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ConnectMapDialog.this.dismiss();
            }
        });
        return builder.create();
    }

    private boolean dataIsValid()
    {
        return !etID.getText().toString().isEmpty();
    }
}
