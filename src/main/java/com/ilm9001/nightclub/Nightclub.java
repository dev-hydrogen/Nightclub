package com.ilm9001.nightclub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.json.LightJSONReader;
import com.ilm9001.nightclub.json.LightJSONWriter;
import com.ilm9001.nightclub.light.LightUniverse;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public final class Nightclub extends JavaPlugin {
    @Getter private static Nightclub instance;
    @Getter private LightJSONReader JSONreader;
    @Getter private LightJSONWriter JSONwriter;
    @Getter private Gson GSON;
    
    public static final String JSON_FILE_NAME = "lights.json";
    public static File DATA_FOLDER;
    
    @SneakyThrows
    @Override
    public void onEnable() {
        GSON = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LightUniverse.class, new LightUniverse.LightUniverseInstanceCreator())
                .create();
        
        instance = this;
        JSONreader = new LightJSONReader(GSON);
        JSONwriter = new LightJSONWriter(GSON);
        
        this.saveDefaultConfig();
        
        DATA_FOLDER = this.getDataFolder();
        
        if(!JSONwriter.createLightJSON()) {
            this.getLogger().log(Level.SEVERE, "Could not create Light JSON file! Disabling");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        
        JSONwriter.put(new LightUniverse());
        JSONwriter.put(new LightUniverse());
        JSONwriter.put(new LightUniverse());
        
        this.getLogger().info(""+JSONreader.getLastUniverse());
        this.getLogger().info(""+JSONreader.getUniverses().get(0).toString());
        this.getLogger().info(""+JSONreader.getUniverses().get(1).toString());
        this.getLogger().info(""+JSONreader.getUniverses().get(2).toString());
        
        // Plugin startup logic
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
}
