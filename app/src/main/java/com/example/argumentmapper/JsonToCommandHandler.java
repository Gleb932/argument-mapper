package com.example.argumentmapper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;

public class JsonToCommandHandler {
    ArgumentMap map;
    Gson gson;

    public static class ConversionException extends Exception{}

    public JsonToCommandHandler(ArgumentMap map, Gson gson)
    {
        this.gson = gson;
        this.map = map;
    }

    private ArrayList<Short> convertPath(JsonArray path)
    {
        ArrayList<Short> numPath = new ArrayList<>(path.size());
        for(int i = 0; i < path.size(); i++)
        {
            numPath.add(path.get(i).getAsShort());
        }
        return numPath;
    }

    public Command jsonToCommand(String jsonCommand) throws ConversionException {
        JsonObject json = gson.fromJson(jsonCommand, JsonObject.class);
        String operation = json.get("operation").getAsString();
        switch (operation)
        {
            case "addChild": {
                MapNode child = gson.fromJson(json.get("child"), MapNode.class);
                ArrayList<Short> path = convertPath(json.get("parentPath").getAsJsonArray());
                MapNode parent = map.getNode(path);
                return new Command(Command.CommandType.ADD_CHILD, Arrays.asList(parent, child), true);
            }
            case "replaceNode": {
                MapNode newNode = gson.fromJson(json.get("newNode"), MapNode.class);
                ArrayList<Short> path = convertPath(json.get("nodePath").getAsJsonArray());
                MapNode oldNode = map.getNode(path);
                return new Command(Command.CommandType.REPLACE_NODE, Arrays.asList(oldNode, newNode), true);
            }
            case "removeNode": {
                ArrayList<Short> path = convertPath(json.get("nodePath").getAsJsonArray());
                MapNode node = map.getNode(path);
                return new Command(Command.CommandType.REMOVE_NODE, Arrays.asList(node), true);
            }
        }
        throw new ConversionException();
    }
}
