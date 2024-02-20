package com.example.argumentmapper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class CommandToJsonHandler {
    private final Gson gson;

    public CommandToJsonHandler(Gson gson)
    {
        this.gson = gson;
    }

    public JsonObject process(Command input) {
        List<MapNode> nodes = input.nodes;
        JsonObject jsonRequest = new JsonObject();
        switch (input.type)
        {
            case ADD_CHILD: {
                MapNode parent = nodes.get(0);
                MapNode child = nodes.get(1);
                JsonArray parentPath = gson.toJsonTree(parent.getPath()).getAsJsonArray();
                JsonObject childJson = gson.toJsonTree(child).getAsJsonObject();
                jsonRequest.addProperty("operation", "addChild");
                jsonRequest.add("parentPath", parentPath);
                jsonRequest.add("child", childJson);
                break;
            }
            case REPLACE_NODE: {
                MapNode oldNode = nodes.get(0);
                MapNode newNode = nodes.get(1);
                JsonArray nodePath = gson.toJsonTree(oldNode.getPath()).getAsJsonArray();
                JsonObject nodeJson = gson.toJsonTree(newNode).getAsJsonObject();
                jsonRequest.addProperty("operation", "replaceNode");
                jsonRequest.add("nodePath", nodePath);
                jsonRequest.add("newNode", nodeJson);
                break;
            }
            case REMOVE_NODE: {
                MapNode nodeToRemove = nodes.get(0);
                jsonRequest.addProperty("operation", "removeNode");
                JsonArray nodePath = gson.toJsonTree(nodeToRemove.getPath()).getAsJsonArray();
                jsonRequest.add("nodePath", nodePath);
                break;
            }
        }
        return jsonRequest;
    }
}
