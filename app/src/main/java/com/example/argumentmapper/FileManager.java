package com.example.argumentmapper;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

public class FileManager {
    private final Context context;
    private final Gson gson;

    @Inject
    FileManager(Context context, Gson gson)
    {
        this.context = context;
        this.gson = gson;
    }

    public void saveMapToFile(ArgumentMap map)
    {
        try {
            FileOutputStream fout = new FileOutputStream(context.getFilesDir() + "/" + map.getFilename());
            fout.write(gson.toJson(map).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArgumentMap> loadArgumentMaps() {
        File dir = context.getFilesDir();
        File[] subFiles = dir.listFiles();
        ArrayList<ArgumentMap> out = new ArrayList<>();

        if (subFiles != null)
        {
            for (File file : subFiles)
            {
                String filename = file.getName();
                int index = filename.lastIndexOf('.');
                if (index <= 0 || !filename.substring(index + 1).equals(ArgumentMap.getExtension()))
                    continue;

                try {
                    FileReader fileReader = new FileReader(file);
                    ArgumentMap map = gson.fromJson(fileReader, ArgumentMap.class);
                    fileReader.close();
                    map.setFilename(filename);
                    out.add(map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out;
    }

    public void deleteArgumentMap(ArgumentMap map) {
        context.deleteFile(map.getFilename());
    }
}
