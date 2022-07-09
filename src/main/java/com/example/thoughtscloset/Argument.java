package com.example.thoughtscloset;

import java.util.List;

public class Argument extends Thought{
    public Argument(String reflection, boolean proOrCon, List<String> tags)
    {
        super(reflection, tags);
        this.proOrCon = proOrCon;
    }
    boolean proOrCon;
}
