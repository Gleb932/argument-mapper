package com.example.argumentmapper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.argumentmapper.InductiveNode;
import com.example.argumentmapper.R;

public class AddInductiveNodeDialog extends AppCompatDialogFragment {

    private EditText etName, etDescription, etWeight;
    private TextView tvConclusion;
    private AddNodeDialogListener listener;
    private InductiveNode editingNode;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_add_inductive_node, null);
        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etWeight = view.findViewById(R.id.etWeight);
        tvConclusion = view.findViewById(R.id.tvConclusion);

        if(editingNode != null) {
            etName.setText(editingNode.getName());
            etDescription.setText(editingNode.getDescription());
            etWeight.setText(Integer.toString(editingNode.getWeight()));
            if (editingNode.getParent() == null) etWeight.setFocusable(false);
            tvConclusion.setText("Conclusion: " + editingNode.getConclusion());
            tvConclusion.setVisibility(View.VISIBLE);
        }

        builder.setView(view)
        .setPositiveButton(((editingNode!=null)?"Change":"Create"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(!dataIsValid())
                {
                    Toast.makeText(getActivity(), (etDescription.getText().toString().isEmpty()?"Empty description":"Empty weight"), Toast.LENGTH_SHORT).show();
                    return;
                }
                InductiveNode newItem = new InductiveNode(etDescription.getText().toString(), etName.getText().toString(), Integer.parseInt(etWeight.getText().toString()));
                if(editingNode != null)
                {
                    listener.onFinishEditingNode(newItem);
                }else {
                    listener.onFinishCreatingNode(newItem);
                }
            }
        })
        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddInductiveNodeDialog.this.dismiss();
                if(editingNode != null)
                {
                    listener.onFinishEditingNode(null);
                }else {
                    listener.onFinishCreatingNode(null);
                }
            }
        });
        return builder.create();
    }

    public void setEditingNode(InductiveNode node)
    {
        editingNode = node;
    }
    public void setListener(AddNodeDialogListener listener)
    {
        this.listener = listener;
    }

    boolean dataIsValid()
    {
        return !etDescription.getText().toString().isEmpty() && !etWeight.getText().toString().isEmpty();
    }
}
