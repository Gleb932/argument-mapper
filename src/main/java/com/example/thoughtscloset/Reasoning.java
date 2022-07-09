package com.example.thoughtscloset;

import java.util.ArrayList;
import java.util.List;

public class Reasoning implements RecyclerViewItem{
    private String topic;
    private boolean conclusion;
    List<String> tags;
    private List<Argument> arguments;

    Reasoning(String topic, List<String> tags)
    {
        this.topic = topic;
        this.arguments = new ArrayList<>();
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

    List<Argument> getArguments()
    {
        return arguments;
    }

    void addArgument(Argument argument)
    {
        arguments.add(argument);
    }

    @Override
    public int getType() {
        return REASONING;
    }
}
