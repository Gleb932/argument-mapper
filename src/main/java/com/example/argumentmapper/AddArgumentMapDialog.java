package com.example.argumentmapper;

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

public class AddArgumentMapDialog extends AppCompatDialogFragment {

    private EditText etTopic, etDescription;
    private AddArgumentMapDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        listener = (AddArgumentMapDialogListener)activity;

        final View view = inflater.inflate(R.layout.dialog_add_map, null);
        etTopic = view.findViewById(R.id.etTopic);
        etDescription = view.findViewById(R.id.etDescription);

        builder.setView(view)
        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(!dataIsValid())
                {
                    Toast.makeText(getActivity(), "Description is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArgumentMap newItem = new ArgumentMap(etDescription.getText().toString(), etTopic.getText().toString());
                listener.onFinishAddArgumentMapDialog(newItem);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddArgumentMapDialog.this.dismiss();
            }
        });
        return builder.create();
    }

    private boolean dataIsValid()
    {
        return !etDescription.getText().toString().isEmpty();
    }
}
