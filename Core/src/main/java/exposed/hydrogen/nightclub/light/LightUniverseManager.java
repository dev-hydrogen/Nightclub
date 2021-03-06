package exposed.hydrogen.nightclub.light;

import exposed.hydrogen.nightclub.Nightclub;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal runtime storage of LightUniverses, gets saved to disk sometimes.
 */
public class LightUniverseManager {
    @Getter private final List<LightUniverse> universes;
    @Getter @Setter private LightUniverse loadedUniverse;

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
