package com.example.thoughtscloset;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddItemDialog extends AppCompatDialogFragment {

    RadioGroup radioGroup;
    EditText editText1, editText2;
    int itemType;
    AddItemDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        listener = (AddItemDialogListener)activity;

        final View view = inflater.inflate(R.layout.dialog_add, null);
        editText1 = view.findViewById(R.id.editText1);
        editText2 = view.findViewById(R.id.editText2);

        builder.setView(view)
                .setPositiveButton(R.string.dialog_add_submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(itemType == RecyclerViewItem.THOUGHT)
                        {
                            List<String> tags;
                            if(editText2.getText().toString().isEmpty())
                                tags = new ArrayList();
                            else
                                tags = new ArrayList(Arrays.asList(editText2.getText().toString().split(" ")));
                            Thought newItem = new Thought(editText1.getText().toString(), tags);
                            listener.onFinishAddItemDialog(newItem);
                        }else if(itemType == RecyclerViewItem.REASONING)
                        {
                            List<String> tags;
                            if(editText2.getText().toString().isEmpty())
                                tags = new ArrayList();
                            else
                                tags = new ArrayList(Arrays.asList(editText2.getText().toString().split(" ")));
                            Reasoning newItem = new Reasoning(editText1.getText().toString(), tags);
                            listener.onFinishAddItemDialog(newItem);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_add_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddItemDialog.this.dismiss();
                    }
                });

        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                editText1.getText().clear();
                editText2.getText().clear();
                if(i == R.id.radioThought)
                {
                    editText1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    //editText1.setMaxLines(10);
                    itemType = RecyclerViewItem.THOUGHT;
                }
                else if(i == R.id.radioReasoning)
                {
                    editText1.setInputType(InputType.TYPE_CLASS_TEXT);
                    itemType = RecyclerViewItem.REASONING;
                }
            }
        });
        radioGroup.check(R.id.radioReasoning);
        itemType = RecyclerViewItem.REASONING;
        return builder.create();
    }
}
