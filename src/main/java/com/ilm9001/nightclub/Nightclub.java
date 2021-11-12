package com.ilm9001.nightclub;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.commands.BeatmapCommand;
import com.ilm9001.nightclub.commands.LightCommand;
import com.ilm9001.nightclub.commands.LightUniverseCommand;
import com.ilm9001.nightclub.json.LightJSONReader;
import com.ilm9001.nightclub.json.LightJSONWriter;
import com.ilm9001.nightclub.light.Light;
import com.ilm9001.nightclub.light.LightType;
import com.ilm9001.nightclub.light.LightUniverse;
import com.ilm9001.nightclub.light.LightUniverseManager;
import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightSpeedChannel;
import com.ilm9001.nightclub.light.pattern.LightPattern;
import com.ilm9001.nightclub.util.Util;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
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
                .registerTypeAdapter(Light.class, new Light.LightUniverseInstanceCreator())
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
        
        commandManager.getCommandCompletions().registerCompletion("pattern", c -> Util.getStringValuesFromEnum(LightPattern.class));
        commandManager.getCommandCompletions().registerCompletion("type", c -> Util.getStringValuesFromEnum(LightType.class));
        commandManager.getCommandCompletions().registerCompletion("channels", c -> Util.getStringValuesFromEnum(LightChannel.class));
        commandManager.getCommandCompletions().registerCompletion("speedchannels", c -> Util.getStringValuesFromEnum(LightSpeedChannel.class));
        commandManager.getCommandCompletions().registerCompletion("beatmaps", c -> {
            File[] directories = new File(getDataFolder().getAbsolutePath()).listFiles(File::isDirectory);
            Collection<String> strings = new ArrayList<>();
            for (File f : directories) {
                strings.add(f.getName());
            }
            return strings;
        });
        commandManager.getCommandCompletions().registerCompletion("universes", c -> {
            Collection<LightUniverse> lightUniverses = getLightUniverseManager().getUniverses();
            Collection<String> strings = new ArrayList<>();
            lightUniverses.forEach(lu -> strings.add(lu.getName()));
            return strings;
        });
        commandManager.getCommandCompletions().registerCompletion("lights", c -> {
            Collection<Light> lights = getLightUniverseManager().getLoadedUniverse().getLights();
            Collection<String> strings = new ArrayList<>();
            lights.forEach(l -> strings.add(l.getName()));
            return strings;
        });
        
        commandManager.registerCommand(new LightCommand());
        commandManager.registerCommand(new BeatmapCommand());
        commandManager.registerCommand(new LightUniverseCommand());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        lightUniverseManager.save();
    }
    
}
