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

import java.util.List;

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RecyclerViewItem> items;
    private LayoutInflater inflater;
    private Context context;

    class ReasoningViewHolder extends RecyclerView.ViewHolder {
        TextView vTopic, vConclusion;
        ChipGroup vChipGroup;
        ReasoningViewHolder(View v) {
            super(v);
            vTopic = v.findViewById(R.id.topic);
            vConclusion = v.findViewById(R.id.conclusion);
            vChipGroup = v.findViewById(R.id.chipGroup);
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
        switch(type) {
            case RecyclerViewItem.THOUGHT:
                return new ThoughtViewHolder(inflater.inflate(R.layout.thought_card, viewGroup, false));
            case RecyclerViewItem.REASONING:
                return new ReasoningViewHolder(inflater.inflate(R.layout.reasoning_card, viewGroup, false));
            default:
                return new ThoughtViewHolder(inflater.inflate(R.layout.thought_card, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        RecyclerViewItem temp = items.get(i);
        switch(temp.getType())
        {
            case RecyclerViewItem.THOUGHT: {
                Thought item = (Thought) temp;
                ThoughtViewHolder vh = (ThoughtViewHolder) viewHolder;
                vh.vThought.setText(item.description);
                vh.vChipGroup.removeAllViews();
                ViewGroup.LayoutParams chipParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                for(String tag: item.tags) {
                    Chip tempChip = new Chip(context);
                    tempChip.setText(tag);
                    tempChip.setLayoutParams(chipParams);
                    vh.vChipGroup.addView(tempChip);
                }
                break;
            }
            case RecyclerViewItem.REASONING: {
                Reasoning item = (Reasoning) temp;
                ReasoningViewHolder vh = (ReasoningViewHolder) viewHolder;
                vh.vTopic.setText(item.getTopic());
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
