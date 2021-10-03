package com.ilm9001.nightclub.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.light.LightUniverse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class JSONUtils {
    public static final File LIGHT_JSON = new File(Nightclub.DATA_FOLDER +"/"+ Nightclub.JSON_FILE_NAME);
    
    public static Reader getReader(File file) {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
