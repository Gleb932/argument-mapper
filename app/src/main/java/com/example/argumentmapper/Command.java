package com.example.argumentmapper;

import java.util.List;

public class Command {
    public enum CommandType{
        ADD_CHILD,
        REMOVE_NODE,
        REPLACE_NODE
    }
    public Command(CommandType type, List<MapNode> nodes)
    {
        this.type = type;
        this.nodes = nodes;
    }
    public Command(CommandType type, List<MapNode> nodes, boolean external)
    {
        this.type = type;
        this.nodes = nodes;
        this.external = external;
    }
    public CommandType type;
    public List<MapNode> nodes;
    public boolean external;
}