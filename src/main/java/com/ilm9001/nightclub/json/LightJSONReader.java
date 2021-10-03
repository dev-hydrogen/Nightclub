package com.ilm9001.nightclub.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilm9001.nightclub.light.LightUniverse;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LightJSONReader {
    private final Gson gson;
    
    public LightJSONReader(Gson gson) {
        this.gson = gson;
    }
    
    public LightUniverse getLastUniverse() {
        List<LightUniverse> universes = new ArrayList<>();
        try {
            universes = getUniverses();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(universes == null || universes.isEmpty()) { return new LightUniverse(new ArrayList<>(),UUID.randomUUID(),0,"Unnamed-Universe"); }
        return universes.get(universes.size()-1);
    }
    
    @SuppressWarnings({"UnstableApiUsage"}) // suppress warnings about TypeToken being in beta
    public @Nullable List<LightUniverse> getUniverses() throws IOException {
        Reader reader = JSONUtils.getReader(JSONUtils.LIGHT_JSON);
        if (reader == null) {return null;}
        Type lightUniverseType = new TypeToken<List<LightUniverse>>(){}.getType();
        ArrayList<LightUniverse> universes = gson.fromJson(reader,lightUniverseType);
        reader.close();
        return universes;
    }
    
    public @Nullable LightUniverse getUniverse(UUID id) throws IOException {
        Reader reader = JSONUtils.getReader(JSONUtils.LIGHT_JSON);
        List<LightUniverse> universes = getUniverses();
        if(universes == null || reader == null) {return null;}
        
        LightUniverse returnverse = universes.stream()
                .filter(Objects::nonNull)
                .filter(universe -> id == universe.getUniqueID())
                .findFirst()
                .orElse(null);
        reader.close();
        return returnverse;
    }
}
