package com.ilm9001.nightclub.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.ilm9001.nightclub.light.LightUniverse;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Reader for lights.json where LightUniverses and Lights are stored through restarts.
 */
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
        if (universes.isEmpty()) {
            return new LightUniverse(new ArrayList<>(), UUID.randomUUID(), 0, "Unnamed-Universe", Bukkit.getWorlds().get(0));
        }
        return universes.get(universes.size() - 1);
    }
    
    @SuppressWarnings({"UnstableApiUsage"}) // suppress warnings about TypeToken being in beta
    public @NotNull List<LightUniverse> getUniverses() throws IOException {
        Reader reader = JSONUtils.getReader(JSONUtils.LIGHT_JSON);
        if (reader == null) {
            return new ArrayList<>();
        }
        
        Type lightUniverseType = new TypeToken<List<LightUniverse>>() {
        }.getType();
        ArrayList<LightUniverse> universes = gson.fromJson(reader, lightUniverseType);
        if (universes == null) {
            universes = new ArrayList<>();
        }
        
        reader.close();
        return universes;
    }
    
    public @Nullable LightUniverse getUniverse(UUID id) throws IOException {
        Reader reader = JSONUtils.getReader(JSONUtils.LIGHT_JSON);
        List<LightUniverse> universes = getUniverses();
        if (reader == null) {
            return null;
        }
        
        LightUniverse returnverse = universes.stream()
                .filter(Objects::nonNull)
                .filter(universe -> id == universe.getUniqueID())
                .findFirst()
                .orElse(null);
        reader.close();
        return returnverse;
    }
}
