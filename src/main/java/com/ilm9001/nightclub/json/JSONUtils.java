package com.ilm9001.nightclub.json;

import com.ilm9001.nightclub.Nightclub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JSONUtils {
    public static final File LIGHT_JSON = new File(Nightclub.DATA_FOLDER + "/" + Nightclub.JSON_FILE_NAME);
    
    public static FileReader getReader(File file) {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
