package com.ilm9001.nightclub;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.commands.LightCommand;
import com.ilm9001.nightclub.json.LightJSONReader;
import com.ilm9001.nightclub.json.LightJSONWriter;
import com.ilm9001.nightclub.light.LightUniverse;
import com.ilm9001.nightclub.light.LightUniverseManager;
import com.ilm9001.nightclub.light.pattern.LightPattern;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
        
        DATA_FOLDER = this.getDataFolder();
        
        saveDefaultConfig();
        
        if (!JSONwriter.createLightJSON()) {
            this.getLogger().log(Level.SEVERE, "Could not create Light JSON file! Disabling");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        
        lightUniverseManager = new LightUniverseManager();
        
        commandManager = new PaperCommandManager(this);
        
        commandManager.getCommandCompletions().registerCompletion("pattern", c -> {
            Collection<LightPattern> collection = new ArrayList<>(Arrays.asList(LightPattern.values()));
            Collection<String> strings = new ArrayList<>();
            collection.forEach((pattern) -> strings.add(pattern.toString()));
            return strings;
        });
        commandManager.registerCommand(new LightCommand());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        lightUniverseManager.save();
    }
    
}
