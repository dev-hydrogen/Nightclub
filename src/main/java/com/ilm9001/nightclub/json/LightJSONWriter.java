package com.ilm9001.nightclub.json;

import com.google.gson.Gson;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.light.LightUniverse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static com.ilm9001.nightclub.Nightclub.DATA_FOLDER;
import static com.ilm9001.nightclub.Nightclub.JSON_FILE_NAME;

/*
 * On spigot, there might not be much point to making a seperate json file in favor of a custom FileConfiguration, but this allows
 * for easier custom object deserialization and serialization compared to yml, aswell as will make future (possible) minestom support easier to implement
 */

/**
 * Writer for lights.json where LightUniverses and Lights are stored through restarts.
 */
public class LightJSONWriter {
    private final Gson gson;
    
    public LightJSONWriter(Gson gson) {
        this.gson = gson;
    }
    public boolean lightJSONexists() {
        return JSONUtils.LIGHT_JSON.isFile();
    }
    
    /**
     * Checks if a lights.json file exists, if one can't be found, creates one.
     *
     * @return if the creation was successful, or the file already exists
     * @throws IOException if for some reason the newly created file can't be modified or made
     */
    public boolean createLightJSON() throws IOException {
        if (lightJSONexists()) {
            return true;
        }
        if (!JSONUtils.LIGHT_JSON.createNewFile()) {
            return false;
        }
        
        put(new LightUniverse());
        
        Nightclub.getInstance().getLogger().info("JSON File created at " + DATA_FOLDER + "/" + JSON_FILE_NAME);
        return true;
    }
    /**
     * Add a new LightUniverse to the file
     *
     * @param universe LightUniverse to store
     */
    public void put(LightUniverse universe) {
        try {
            List<LightUniverse> universes = Nightclub.getJSONreader().getUniverses();
            universes.add(universe);
            set(universes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Mainly used by the LightUniverseManager, this will set & overwrite the file to the List provided. Use with caution.
     *
     * @param universes List of LightUniverses to set the file to contain
     * @throws IOException If file can not for some reason be modified
     */
    public void set(List<LightUniverse> universes) throws IOException {
        Writer writer = new FileWriter(JSONUtils.LIGHT_JSON);
        gson.toJson(universes, writer);
        writer.close();
    }
}
