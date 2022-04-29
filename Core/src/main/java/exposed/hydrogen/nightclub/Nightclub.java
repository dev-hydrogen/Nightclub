package exposed.hydrogen.nightclub;

import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.hypera.chameleon.core.Chameleon;
import dev.hypera.chameleon.core.ChameleonPlugin;
import dev.hypera.chameleon.core.data.PluginData;
import exposed.hydrogen.nightclub.commands.BeatmapCommand;
import exposed.hydrogen.nightclub.commands.LightCommand;
import exposed.hydrogen.nightclub.commands.LightUniverseCommand;
import exposed.hydrogen.nightclub.json.LightJSONReader;
import exposed.hydrogen.nightclub.json.LightJSONWriter;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.LightUniverse;
import exposed.hydrogen.nightclub.light.LightUniverseManager;
import exposed.hydrogen.nightclub.light.data.LightData;
import exposed.hydrogen.nightclub.light.data.LightPattern;
import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.light.event.LightChannel;
import exposed.hydrogen.nightclub.light.event.LightSpeedChannel;
import exposed.hydrogen.nightclub.util.CrossCompatUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static exposed.hydrogen.nightclub.util.Util.getStringValuesFromArray;


public final class Nightclub extends ChameleonPlugin {
    public static final String JSON_FILE_NAME = "lights.json";
    public static File DATA_FOLDER;
    @Getter private static Chameleon chameleon;
    @Getter private static Nightclub instance;
    @Getter private static LightJSONReader JSONreader;
    @Getter private static LightJSONWriter JSONwriter;
    @Getter private static Gson GSON;
    @Getter private static LightUniverseManager lightUniverseManager;
    @Getter private static final PluginData pluginData;
    @Getter @Setter private static CrossCompatUtil crossCompatUtil;

    public Nightclub(@NotNull Chameleon chameleon) {
        super(chameleon);
    }


    @SneakyThrows
    @Override
    public void onEnable() {
        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LightUniverse.class, new LightUniverse.LightUniverseInstanceCreator())
                .registerTypeAdapter(Light.class, new Light.LightInstanceCreator())
                .registerTypeAdapter(LightData.class, new LightData.LightDataInstanceCreator())
                .create();

        chameleon = getChameleon();

        DATA_FOLDER = chameleon.getDataFolder().toFile();

        instance = this;
        JSONreader = new LightJSONReader(GSON);
        JSONwriter = new LightJSONWriter(GSON);

        lightUniverseManager = new LightUniverseManager();
    }

    @Override
    public void onDisable() {
        lightUniverseManager.save();
    }

    public static void registerCommands(CommandManager<?, ?, ?, ?, ?, ?> commandManager) {
        commandManager.registerCommand(new LightCommand());
        commandManager.registerCommand(new BeatmapCommand());
        commandManager.registerCommand(new LightUniverseCommand());
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
                "Nightclub",
                List.of(PluginData.Platform.MINESTOM, PluginData.Platform.SPIGOT)
        );
    }

}
