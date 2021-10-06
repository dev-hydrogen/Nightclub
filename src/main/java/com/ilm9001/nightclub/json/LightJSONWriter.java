package com.ilm9001.nightclub.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.light.LightUniverse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.ilm9001.nightclub.Nightclub.DATA_FOLDER;
import static com.ilm9001.nightclub.Nightclub.JSON_FILE_NAME;

/**
 * On spigot, there might not be much point to making a seperate json file in favor of a custom FileConfiguration, but this allows
 * for easier custom object deserialization and serialization compared to yml, aswell as will make future (possible) minestom support easier to implement
 *
 */
public class LightJSONWriter {
    private final Gson gson;
    
    public LightJSONWriter(Gson gson) {
        this.gson = gson;
    }
    public boolean lightJSONexists() {
        return JSONUtils.LIGHT_JSON.isFile();
    }
    
    public boolean createLightJSON() throws IOException {
        if(lightJSONexists()) {return true;}
        if(!JSONUtils.LIGHT_JSON.createNewFile()) {return false;}
        
        put(new LightUniverse());
        
        Nightclub.getInstance().getLogger().info("JSON File created at " + DATA_FOLDER +"/"+ JSON_FILE_NAME);
        return true;
    }
    public void put(LightUniverse universe) throws IOException {
        List<LightUniverse> universes = Nightclub.getJSONreader().getUniverses();
        Writer writer = new FileWriter(JSONUtils.LIGHT_JSON);
        universes.add(universe);
        gson.toJson(universes,writer);
        writer.close();
    }
    
    public void set(List<LightUniverse> universes) throws IOException {
        Writer writer = new FileWriter(JSONUtils.LIGHT_JSON);
        gson.toJson(universes,writer);
        writer.close();
    }
}
