package com.example.argumentmapper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.argumentmapper.DeductiveNode;
import com.example.argumentmapper.R;

public class AddDeductiveNodeDialog extends AppCompatDialogFragment {

    private Spinner spinner;
    private TextView tvConclusion;
    private AddNodeDialogListener listener;
    private DeductiveNode editingNode;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_add_deductive_node, null);
        spinner = view.findViewById(R.id.spinner);
        tvConclusion = view.findViewById(R.id.tvConclusion);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.node_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(editingNode != null) {
            spinner.setSelection(editingNode.getOperator());
            tvConclusion.setText("Conclusion: " + editingNode.getConclusion());
            tvConclusion.setVisibility(View.VISIBLE);
        }

        builder.setView(view)
        .setPositiveButton(((editingNode!=null)?"Change":"Create"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                DeductiveNode newItem = new DeductiveNode((byte)spinner.getSelectedItemPosition());
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
                AddDeductiveNodeDialog.this.dismiss();
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

    public void setEditingNode(DeductiveNode node)
    {
        editingNode = node;
    }
    public void setListener(AddNodeDialogListener listener)
    {
        this.listener = listener;
    }
}
