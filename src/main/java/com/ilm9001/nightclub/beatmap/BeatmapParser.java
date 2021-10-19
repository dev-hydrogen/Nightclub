package com.ilm9001.nightclub.beatmap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ilm9001.nightclub.Nightclub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class BeatmapParser {
    /**
     * Get the info.dat file information (bpm, artist, song name, level author)
     *
     * @param name Name of the folder the info.dat file is in, should be spelled exactly as is on file, usually without uppercase letters.
     * @return InfoData which includes bpm, artist, song name and the beatmaps author.
     * Returns null if no info.dat file can be found.
     */
    public static @Nullable InfoData getInfoData(String name) {
        File dataFolder = Nightclub.DATA_FOLDER;
        String infoFile = dataFolder + "/" + name + "/info.dat";
        JsonObject info;
        JsonArray difficultyBeatmapSets;
        String filename = "";
        
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
            String difficulty = ((JsonObject) element).get("_beatmapCharacteristicName").getAsString();
            filename = ((JsonObject) element).get("_difficultyBeatmaps").getAsJsonObject().get("_beatmapFilename").getAsString();
            if (difficulty.contains("Lightshow")) {
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
        File beatMapFile = new File(dataFolder + "/" + name + "/" + info.getBeatmapFileName() + ".dat");
        double bpm = info.getBeatsPerMinute().doubleValue();
        
        if (!beatMapFile.isFile()) {
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
        
        eventObject.forEach(obj -> events.add(new LightEvent((JsonObject) obj, bpm)));
        
        return events;
    }
}
