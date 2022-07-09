package com.example.thoughtscloset;

import java.util.List;

class Thought implements RecyclerViewItem {
    Thought(String description, List<String> tags)
    {
        this.description = description;
        this.tags = tags;
    }
    String description;
    List<String> tags;

    @Override
    public int getType() {
        return THOUGHT;
    }
}
