package com.example.thoughtscloset;

import java.util.ArrayList;
import java.util.List;

public class Reasoning implements RecyclerViewItem{
    private String topic;
    private boolean conclusion;
    List<String> tags;
    private List<Thought> thoughts;

    Reasoning(String topic, List<String> tags)
    {
        this.topic = topic;
        this.thoughts = new ArrayList<>();
        this.tags = tags;
    }

    String getTopic()
    {
        return topic;
    }

    boolean getConclusion()
    {
        return conclusion;
    }

    List<Thought> getThoughts()
    {
        return thoughts;
    }

    void addThought(Thought thought)
    {
        thoughts.add(thought);
    }

    @Override
    public int getType() {
        return REASONING;
    }
}
