package com.example.argumentmapper;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileManager {
    private static Context context;

    public static void setContext(Context context) {
        FileManager.context = context;
    }

    public static void saveMapToFile(ArgumentMap map)
    {
        String filename = map.getFilename();
        Gson gson = new Gson();
        String fileContents = gson.toJson(map.getRoot(), InductiveNode.class);
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArgumentMap> loadArgumentMaps() {
        FileInputStream fis = null;
        File dir = context.getFilesDir();
        File[] subFiles = dir.listFiles();
        ArrayList<ArgumentMap> out = new ArrayList<>();

        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<InductiveNode> deserializer = new JsonDeserializer<InductiveNode>() {
            @Override
            public InductiveNode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonNode = json.getAsJsonObject();
                InductiveNode node = new InductiveNode(jsonNode.get("description").getAsString(), jsonNode.get("name").getAsString(), jsonNode.get("weight").getAsInt());
                JsonArray jsonChildren = jsonNode.get("children").getAsJsonArray();
                for(int i = 0; i < jsonChildren.size(); i++)
                {
                    node.addChild(deserialize(jsonChildren.get(i), typeOfT, context));
                }
                return node;
            }
        };
        gsonBuilder.registerTypeAdapter(InductiveNode.class, deserializer);
        Gson gson = gsonBuilder.create();

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                String filename = file.getName();
                int i = filename.lastIndexOf('.');
                if (i > 0)
                {
                    if (filename.substring(i + 1).equals(ArgumentMap.getExtension())) {
                        try {
                            fis = context.openFileInput(file.getName());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        InputStreamReader inputStreamReader =
                                new InputStreamReader(fis, StandardCharsets.UTF_8);
                        StringBuilder stringBuilder = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                            String line = reader.readLine();
                            while (line != null) {
                                stringBuilder.append(line);
                                line = reader.readLine();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            InductiveNode mapRoot = gson.fromJson(stringBuilder.toString(), InductiveNode.class);
                            ArgumentMap map = new ArgumentMap(mapRoot, filename);
                            out.add(map);
                        }
                    }
                }
            }
        }
        return out;
    }

    public static void deleteArgumentMap(ArgumentMap map) {
        context.deleteFile(map.getFilename());
    }
}
