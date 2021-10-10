package com.ilm9001.nightclub;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.commands.BeatmapCommand;
import com.ilm9001.nightclub.commands.LightCommand;
import com.ilm9001.nightclub.json.LightJSONReader;
import com.ilm9001.nightclub.json.LightJSONWriter;
import com.ilm9001.nightclub.light.LightType;
import com.ilm9001.nightclub.light.LightUniverse;
import com.ilm9001.nightclub.light.LightUniverseManager;
import com.ilm9001.nightclub.light.event.LightChannel;
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
    @Getter private static Nightclub instance;
    @Getter private static LightJSONReader JSONreader;
    @Getter private static LightJSONWriter JSONwriter;
    @Getter private static Gson GSON;
    @Getter private static LightUniverseManager lightUniverseManager;
    @Getter private static PaperCommandManager commandManager;
    
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
        commandManager.getCommandCompletions().registerCompletion("type", c -> {
            Collection<LightType> collection = new ArrayList<>(Arrays.asList(LightType.values()));
            Collection<String> strings = new ArrayList<>();
            collection.forEach((pattern) -> strings.add(pattern.toString()));
            return strings;
        });
        commandManager.getCommandCompletions().registerCompletion("channels", c -> {
            Collection<LightChannel> collection = new ArrayList<>(Arrays.asList(LightChannel.values()));
            Collection<String> strings = new ArrayList<>();
            collection.forEach((pattern) -> strings.add(pattern.toString()));
            return strings;
        });
        commandManager.getCommandCompletions().registerCompletion("beatmaps", c -> {
            File[] directories = new File(getDataFolder().getAbsolutePath()).listFiles(File::isDirectory);
            Collection<String> strings = new ArrayList<>();
            for (File f : directories) {
                strings.add(f.getName());
            }
            return strings;
        });
        
        commandManager.registerCommand(new LightCommand());
        commandManager.registerCommand(new BeatmapCommand());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        lightUniverseManager.save();
    }
    
}
