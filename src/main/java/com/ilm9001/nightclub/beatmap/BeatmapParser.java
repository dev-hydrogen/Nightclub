package com.ilm9001.nightclub.beatmap;

import com.google.gson.JsonArray;
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
    
    public static @Nullable InfoData getInfoData(String name) {
        File dataFolder = Nightclub.DATA_FOLDER;
        String infoFile = dataFolder + "/" + name + "/info.dat";
        JsonObject info;
        
        try {
            JsonParser parser = new JsonParser();
            FileReader reader = new FileReader(infoFile);
            
            info = (JsonObject) parser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        
        return InfoData.builder()
                .bpm(info.get("_beatsPerMinute").getAsNumber())
                .author(info.get("_songAuthorName").getAsString())
                .song(info.get("_songName").getAsString())
                .mapper(info.get("_levelAuthorName").getAsString())
                .build();
    }
    
    public static @NotNull List<LightEvent> getEvents(String name) {
        File dataFolder = Nightclub.DATA_FOLDER;
        String beatMapFile = dataFolder + "/" + name + "/" + name + ".dat";
        List<LightEvent> events = new ArrayList<>();
        JsonArray eventObject;
        InfoData info = BeatmapParser.getInfoData(name);
        if (info == null) {
            return events;
        }
        double bpm = info.getBeatsPerMinute().doubleValue();
        
        try {
            JsonParser parser = new JsonParser();
            FileReader reader = new FileReader(beatMapFile);
            
            // LOL
            eventObject = (JsonArray) ((JsonObject) parser.parse(reader)).get("_events");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return events;
        }
        
        eventObject.forEach((obj) -> {
            events.add(new LightEvent((JsonObject) obj, bpm));
        });
        
        return events;
    }
}
