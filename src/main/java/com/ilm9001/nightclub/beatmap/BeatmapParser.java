package com.ilm9001.nightclub.beatmap;

import com.google.gson.*;
import com.ilm9001.nightclub.Nightclub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeatmapParser {
    /**
     * Get the info.dat file information (bpm, artist, song folder, level author)
     *
     * @param folder Name of the folder the info.dat file is in, may be case-sensitive on certain filesystems.
     * @return InfoData which includes bpm, artist, song folder and the beatmaps author.
     * Returns null if no info.dat file can be found.
     */
    public static @Nullable InfoData getInfoData(String folder) {
        File dataFolder = Nightclub.DATA_FOLDER;
        File infoFolder = new File(dataFolder + "/" + folder);
        @SuppressWarnings("ConstantConditions") // compiler warns about "infoFolder.listFiles() might be null". please tell me how.
        File infoFile = infoFolder.isDirectory() && infoFolder.listFiles() != null ? Arrays.stream(infoFolder.listFiles())
                .filter(file -> file.getName().equalsIgnoreCase("info.dat"))
                .findFirst().orElse(null) : null;
        if (infoFile == null || !infoFile.isFile() || infoFile.isDirectory()) {
            return null;
        }
        JsonObject info;
        JsonArray difficultyBeatmapSets;
        String filename = "";
        boolean isChroma = false;
        
        try {
            JsonParser parser = new JsonParser();
            FileReader reader = new FileReader(infoFile);
            
            info = (JsonObject) parser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        
        difficultyBeatmapSets = info.getAsJsonArray("_difficultyBeatmapSets");
        for (JsonElement element : difficultyBeatmapSets) {
            String characteristic = ((JsonObject) element).get("_beatmapCharacteristicName").getAsString();
            JsonArray difficultyBeatmaps = ((JsonObject) element).get("_difficultyBeatmaps").getAsJsonArray();
            JsonObject customData = (JsonObject) difficultyBeatmaps.get(5);
            JsonArray requirements;
            if (customData != null) {
                requirements = (JsonArray) customData.get("_requirements");
                if (requirements != null) {
                    isChroma = requirements.contains(new JsonPrimitive("Chroma"))
                            || requirements.contains(new JsonPrimitive("Chroma Lighting Events"))
                            || requirements.contains(new JsonPrimitive("Chroma Special Events"));
                }
            }
            filename = difficultyBeatmaps.get(difficultyBeatmaps.size() - 1).getAsJsonObject().get("_beatmapFilename").getAsString();
            
            if (characteristic.contains("Lightshow") || characteristic.contains("Standard")) {
                break;
            }
        }
        
        return InfoData.builder()
                .bpm(info.get("_beatsPerMinute").getAsNumber())
                .author(info.get("_songAuthorName").getAsString())
                .song(info.get("_songName").getAsString())
                .mapper(info.get("_levelAuthorName").getAsString())
                .songSubName(info.get("_songSubName").getAsString())
                .beatmapFileName(filename)
                .isChroma(isChroma)
                .build();
    }
    /**
     * Get a List of LightEvent's from the folder & filename specified
     *
     * @param name Folder & File name (/name/name.dat/) of the beatmap
     * @return List of LightEvent's in the beatmap file. Returns an empty list if info file can't be found
     */
    public static @NotNull List<LightEvent> getEvents(String name) {
        File dataFolder = Nightclub.DATA_FOLDER;
        List<LightEvent> events = new ArrayList<>();
        JsonArray eventObject;
        InfoData info = BeatmapParser.getInfoData(name);
        if (info == null) {
            return new ArrayList<>();
        }
        File beatMapFile = new File(dataFolder + "/" + name + "/" + info.getBeatmapFileName());
        double bpm = info.getBeatsPerMinute().doubleValue();
        boolean isChroma = info.isChroma();
        if (!beatMapFile.isFile()) {
            // compatability with older versions of the plugin where you had to make sure your difficulty file was spelled exactly the same as the folder it was in
            beatMapFile = new File(dataFolder + "/" + name + "/" + name + ".dat");
            if (!beatMapFile.isFile()) {
                return new ArrayList<>();
            }
        }
        
        try {
            JsonParser parser = new JsonParser();
            FileReader reader = new FileReader(beatMapFile);
            
            // LOL
            eventObject = (JsonArray) ((JsonObject) parser.parse(reader)).get("_events");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return events;
        }
        
        eventObject.forEach(obj -> events.add(new LightEvent((JsonObject) obj, bpm, isChroma)));
        
        return events;
    }
}
