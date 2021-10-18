package com.ilm9001.nightclub.light;

import com.ilm9001.nightclub.Nightclub;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal runtime storage of LightUniverses, gets saved to disk sometimes.
 */
public class LightUniverseManager {
    @Getter private final List<LightUniverse> universes;
    
    /**
     * Adds all LightUniverses found from the LightJSONReader to memory
     */
    public LightUniverseManager() {
        universes = new ArrayList<>();
        try {
            universes.addAll(Nightclub.getJSONreader().getUniverses());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Save current LightUniverse List to disk
     */
    public void save() {
        try {
            Nightclub.getJSONwriter().set(universes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Add and save new LightUniverse
     *
     * @param universe LightUniverse to save
     */
    public void add(LightUniverse universe) {
        universes.add(universe);
        save();
    }
}
