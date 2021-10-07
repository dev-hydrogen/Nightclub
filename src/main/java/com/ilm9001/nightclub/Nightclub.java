package com.ilm9001.nightclub;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.commands.LightCommand;
import com.ilm9001.nightclub.json.LightJSONReader;
import com.ilm9001.nightclub.json.LightJSONWriter;
import com.ilm9001.nightclub.light.LightUniverse;
import com.ilm9001.nightclub.light.LightUniverseManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public final class Nightclub extends JavaPlugin {
    public static final String JSON_FILE_NAME = "lights.json";
    public static File DATA_FOLDER;
    @Getter
    private static Nightclub instance;
    @Getter
    private static LightJSONReader JSONreader;
    @Getter
    private static LightJSONWriter JSONwriter;
    @Getter
    private static Gson GSON;
    @Getter
    private static LightUniverseManager lightUniverseManager;
    @Getter
    private static PaperCommandManager commandManager;
    
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
        
        if (!JSONwriter.createLightJSON()) {
            this.getLogger().log(Level.SEVERE, "Could not create Light JSON file! Disabling");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        
        JSONwriter.put(new LightUniverse());
        JSONwriter.put(new LightUniverse());
        JSONwriter.put(new LightUniverse());
        
        this.getLogger().info("" + JSONreader.getLastUniverse());
        this.getLogger().info("" + JSONreader.getUniverses().get(0).toString());
        this.getLogger().info("" + JSONreader.getUniverses().get(1).toString());
        this.getLogger().info("" + JSONreader.getUniverses().get(2).toString());
        
        lightUniverseManager = new LightUniverseManager();
        
        commandManager = new PaperCommandManager(this);
        
        commandManager.registerCommand(new LightCommand());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        lightUniverseManager.save();
    }
    
}
