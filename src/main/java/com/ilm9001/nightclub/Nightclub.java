package com.ilm9001.nightclub;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.commands.BeatmapCommand;
import com.ilm9001.nightclub.commands.LightCommand;
import com.ilm9001.nightclub.commands.LightUniverseCommand;
import com.ilm9001.nightclub.json.LightJSONReader;
import com.ilm9001.nightclub.json.LightJSONWriter;
import com.ilm9001.nightclub.light.*;
import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightSpeedChannel;
import com.ilm9001.nightclub.util.Easing;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import static com.ilm9001.nightclub.util.Util.getStringValuesFromArray;


public final class Nightclub extends JavaPlugin {
    public static final String JSON_FILE_NAME = "lights.json";
    public static File DATA_FOLDER;
    @Getter private static Nightclub instance;
    @Getter private static LightJSONReader JSONreader;
    @Getter private static LightJSONWriter JSONwriter;
    @Getter private static Gson GSON;
    @Getter private static LightUniverseManager lightUniverseManager;
    @Getter private static PaperCommandManager commandManager;
    @Getter private static ProtocolManager protocolManager;
    
    @SneakyThrows
    @Override
    public void onEnable() {
        GSON = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LightUniverse.class, new LightUniverse.LightUniverseInstanceCreator())
                .registerTypeAdapter(Light.class, new Light.LightUniverseInstanceCreator())
                .create();
        
        protocolManager = ProtocolLibrary.getProtocolManager();
        instance = this;
        JSONreader = new LightJSONReader(GSON);
        JSONwriter = new LightJSONWriter(GSON);
        
        DATA_FOLDER = this.getDataFolder();
        
        saveDefaultConfig();
        
        if (!JSONwriter.createLightJSON()) {
            this.getLogger().log(Level.SEVERE, "Could not create Light JSON file! Disabling");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        
        //register bstats
        Metrics metrics = new Metrics(this, 12300);
        
        lightUniverseManager = new LightUniverseManager();
        
        commandManager = new PaperCommandManager(this);
        
        CommandCompletions<BukkitCommandCompletionContext> completions = commandManager.getCommandCompletions();
        completions.registerCompletion("pattern", c -> getStringValuesFromArray(LightPattern.values()));
        completions.registerCompletion("type", c -> getStringValuesFromArray(LightType.values()));
        completions.registerCompletion("channels", c -> getStringValuesFromArray(LightChannel.values()));
        completions.registerCompletion("speedchannels", c -> getStringValuesFromArray(LightSpeedChannel.values()));
        completions.registerCompletion("easings", c -> getStringValuesFromArray(Easing.values()));
        completions.registerCompletion("beatmaps", c -> getStringValuesFromArray(new File(getDataFolder().getAbsolutePath()).listFiles(File::isDirectory)));
        // https://stackoverflow.com/questions/7909747/why-does-liststring-toarray-return-object-and-not-string-how-to-work-ar
        completions.registerCompletion("universes", c -> {
            if (getLightUniverseManager().getUniverses() != null && !getLightUniverseManager().getUniverses().isEmpty()) {
                return getStringValuesFromArray(getLightUniverseManager().getUniverses().toArray(new LightUniverse[0]));
            } else return new ArrayList<>();
        });
        completions.registerCompletion("lights", c -> {
            if (getLightUniverseManager().getLoadedUniverse() != null) {
                return getStringValuesFromArray(getLightUniverseManager().getLoadedUniverse().getLights().toArray(new Light[0]));
            } else return new ArrayList<>();
        });
        completions.registerCompletion("lightids", c -> {
            if (getLightUniverseManager().getLoadedUniverse() != null && LightCommand.getLight() != null) {
                return getStringValuesFromArray(LightCommand.getLight().getData().getLightIDs().toArray(new Integer[0]));
            } else return new ArrayList<>();
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
