package com.example.thoughtscloset;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RecyclerViewItem> items;
    private LayoutInflater inflater;
    private Context context;
    private static ViewGroup.LayoutParams vhChipParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

    class ReasoningViewHolder extends RecyclerView.ViewHolder {
        TextView vTopic;
        ChipGroup vChipGroup;
        ReasoningViewHolder(View v) {
            super(v);
            vTopic = v.findViewById(R.id.topic);
            vChipGroup = v.findViewById(R.id.chipGroup);
            super.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, vTopic.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        void bindTo(Reasoning reasoning)
        {
            vTopic.setText(reasoning.getTopic());
            vChipGroup.removeAllViews();
            for(String tag: reasoning.tags) {
                Chip tempChip = new Chip(context);
                tempChip.setText(tag);
                tempChip.setLayoutParams(vhChipParams);
                vChipGroup.addView(tempChip);
            }
            super.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, vTopic.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    class ThoughtViewHolder extends RecyclerView.ViewHolder {
        TextView vThought;
        ChipGroup vChipGroup;
        ThoughtViewHolder(View v) {
            super(v);
            vThought = v.findViewById(R.id.thought);
            vChipGroup = v.findViewById(R.id.chipGroup);
        }

        void bindTo(Thought thought)
        {
            vThought.setText(thought.description);
            vChipGroup.removeAllViews();
            for(String tag: thought.tags) {
                Chip tempChip = new Chip(context);
                tempChip.setText(tag);
                tempChip.setLayoutParams(vhChipParams);
                vChipGroup.addView(tempChip);
            }
        }
    }

    MainListAdapter(List<RecyclerViewItem> items, Context context)
    {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int pos)
    {
        return items.get(pos).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View newView;

        switch(type) {
            case RecyclerViewItem.THOUGHT:
                newView = inflater.inflate(R.layout.thought_card, viewGroup, false);
                return new ThoughtViewHolder(newView);
            case RecyclerViewItem.REASONING:
                newView = inflater.inflate(R.layout.reasoning_card, viewGroup, false);
                return new ReasoningViewHolder(newView);
            default:
                newView = inflater.inflate(R.layout.thought_card, viewGroup, false);
                return new ThoughtViewHolder(newView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        RecyclerViewItem temp = items.get(i);
        switch(temp.getType())
        {
            case RecyclerViewItem.THOUGHT: {
                ((ThoughtViewHolder) viewHolder).bindTo((Thought) temp);
                break;
            }
            case RecyclerViewItem.REASONING: {
                ((ReasoningViewHolder) viewHolder).bindTo((Reasoning) temp);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
