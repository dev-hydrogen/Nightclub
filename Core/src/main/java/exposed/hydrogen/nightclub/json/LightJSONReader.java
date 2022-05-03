package exposed.hydrogen.nightclub.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.LightUniverse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

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
            LightUniverse universe = new LightUniverse(new ArrayList<>(), UUID.randomUUID(), 0, "Unnamed-Universe");
            Nightclub.getLightUniverseManager().add(universe);
            return universe;
        }
        return universes.get(universes.size() - 1);
    }

    public @NotNull List<LightUniverse> getUniverses() throws IOException {
        Reader reader = JSONUtils.getReader(JSONUtils.LIGHT_JSON);
        if (reader == null) {
            return new ArrayList<>();
        }

        Type lightUniverseType = new TypeToken<List<LightUniverse>>() {}.getType();
        ArrayList<LightUniverse> universes = gson.fromJson(reader, lightUniverseType);
        if (universes == null) {
            universes = new ArrayList<>();
        }

        // If some data does not exist, add it
        for (LightUniverse universe : universes) {
            List<Light> lights = universe.getLights();
            for (Light light : lights) {
                light.setData(JSONUtils.addNewDataIfNull(light.getData()));
            }
            if(universe.getRings() == null) {
                universe.setRings(new LinkedList<>());
            }
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
