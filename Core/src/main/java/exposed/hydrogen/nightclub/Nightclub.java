package exposed.hydrogen.nightclub;

import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.hypera.chameleon.core.Chameleon;
import dev.hypera.chameleon.core.ChameleonPlugin;
import dev.hypera.chameleon.core.data.PluginData;
import dev.hypera.chameleon.features.configuration.impl.YamlConfiguration;
import exposed.hydrogen.nightclub.commands.BeatmapCommand;
import exposed.hydrogen.nightclub.commands.LightCommand;
import exposed.hydrogen.nightclub.commands.LightUniverseCommand;
import exposed.hydrogen.nightclub.commands.RingCommand;
import exposed.hydrogen.nightclub.json.JSONUtils;
import exposed.hydrogen.nightclub.json.LightJSONReader;
import exposed.hydrogen.nightclub.json.LightJSONWriter;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.LightUniverse;
import exposed.hydrogen.nightclub.light.LightUniverseManager;
import exposed.hydrogen.nightclub.light.Ring;
import exposed.hydrogen.nightclub.light.data.LightPattern;
import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.light.event.LightChannel;
import exposed.hydrogen.nightclub.light.event.LightSpeedChannel;
import exposed.hydrogen.nightclub.util.CrossCompatUtil;
import exposed.hydrogen.nightclub.util.Util;
import exposed.hydrogen.nightclub.wrapper.DebugMarkerFactory;
import exposed.hydrogen.nightclub.wrapper.DebugMarkerWrapper;
import exposed.hydrogen.nightclub.wrapper.LaserFactory;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.sound.SoundRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static exposed.hydrogen.nightclub.util.Util.getStringValuesFromArray;


public final class Nightclub extends ChameleonPlugin {
    public static final String JSON_FILE_NAME = "lights.json";
    public static final String NAMESPACE = "nightclub";
    public static File DATA_FOLDER;
    @Getter private static Chameleon chameleon;
    @Getter private static Nightclub instance;
    @Getter private static LightJSONReader JSONreader;
    @Getter private static LightJSONWriter JSONwriter;
    @Getter private static Gson GSON;
    @Getter private static LightUniverseManager lightUniverseManager;
    @Getter private static final PluginData pluginData;
    @Getter private static SoundRegistry soundRegistry;
    @Getter @NotNull private static YamlConfiguration config;
    @Getter @Setter private static CrossCompatUtil crossCompatUtil;
    @Getter @Setter private static DebugMarkerFactory<? extends DebugMarkerWrapper> markerFactory;
    @Getter @Setter private static LaserFactory<? extends LaserWrapper> laserFactory;

    public Nightclub(@NotNull Chameleon chamel) {
        super(chamel);
        chameleon = chamel;
    }


    @SneakyThrows
    @Override
    public void onEnable() {
        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LightUniverse.class, new LightUniverse.LightUniverseInstanceCreator())
                .create();

        DATA_FOLDER = chameleon.getDataFolder().toFile();

        DATA_FOLDER.mkdir();
        JSONUtils.LIGHT_JSON.createNewFile();
        config = new YamlConfiguration(chameleon.getDataFolder(),"config.yml",true);
        instance = this;
        JSONreader = new LightJSONReader(GSON);
        JSONwriter = new LightJSONWriter(GSON);
        soundRegistry = Util.getSoundRegistry(new File(Nightclub.DATA_FOLDER.getAbsolutePath()));
        Util.addResources();

        lightUniverseManager = new LightUniverseManager();
    }

    @Override
    public void onDisable() {
        lightUniverseManager.save();
    }

    public static <T extends CommandManager<?,?,?,?,?,?>> void registerCommands(T commandManager) {
        commandManager.registerCommand(new LightCommand());
        commandManager.registerCommand(new BeatmapCommand());
        commandManager.registerCommand(new LightUniverseCommand());
        commandManager.registerCommand(new RingCommand());
    }

    public static void registerCompletions(CommandCompletions<?> completions) {
        completions.registerCompletion("patterns", c -> getStringValuesFromArray(LightPattern.values()));
        completions.registerCompletion("types", c -> getStringValuesFromArray(LightType.values()));
        completions.registerCompletion("channels", c -> getStringValuesFromArray(LightChannel.values()));
        completions.registerCompletion("speedchannels", c -> getStringValuesFromArray(LightSpeedChannel.values()));
        completions.registerCompletion("beatmaps", c -> getStringValuesFromArray(new File(DATA_FOLDER.getAbsolutePath()).listFiles(File::isDirectory)));
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
        completions.registerCompletion("rings", c -> {
            if (getLightUniverseManager().getLoadedUniverse() != null) {
                return getStringValuesFromArray(getLightUniverseManager().getLoadedUniverse().getRings().toArray(new Ring[0]));
            } else return new ArrayList<>();
        });
        completions.registerCompletion("lightids", c -> {
            if (getLightUniverseManager().getLoadedUniverse() != null && LightCommand.getLight() != null) {
                return getStringValuesFromArray(LightCommand.getLight().getData().getLightIDs().toArray(new Integer[0]));
            } else return new ArrayList<>();
        });
    }

    static {
        pluginData = new PluginData(
                "Nightclub",
                "1.0.0",
                "Completely customizable lightshows for everyone",
                "hydrogen.exposed",
                List.of("hydrogen"),
                "[Nightclub]",
                List.of(PluginData.Platform.MINESTOM, PluginData.Platform.SPIGOT)
        );
    }

    public static void main(String[] args) {}

}
