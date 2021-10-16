package com.ilm9001.nightclub.light;

import com.ilm9001.nightclub.Nightclub;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LightUniverseManager {
    @Getter private final List<LightUniverse> universes;
    
    public LightUniverseManager() {
        universes = new ArrayList<>();
        try {
            universes.addAll(Nightclub.getJSONreader().getUniverses());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void save() {
        try {
            Nightclub.getJSONwriter().set(universes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void add(LightUniverse universe) {
        universes.add(universe);
        save();
    }
}
