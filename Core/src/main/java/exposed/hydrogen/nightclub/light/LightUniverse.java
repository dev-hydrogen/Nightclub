package exposed.hydrogen.nightclub.light;

import com.google.gson.InstanceCreator;
import exposed.hydrogen.nightclub.GameObject;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.beatmap.json.EnvironmentObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Stores a List of Lights which can be loaded or unloaded at will.
 */
@ToString
public class LightUniverse {
    @Getter private final UUID uniqueID;
    @Getter private final int id;
    @Getter @Setter private String name;
    @Getter private final List<Light> lights;
    @Getter @Setter private List<Ring> rings;
    @Getter private boolean isLoaded;

    public LightUniverse() {
        // pain
        this(
                new ArrayList<>(),
                UUID.randomUUID(),
                Nightclub.getJSONreader().getLastUniverse().getId() + 1,
                "Unnamed-Universe"
        );
    }

    public LightUniverse(List<Light> lights, UUID uniqueID, int id, String name) {
        this(
                lights,
                new LinkedList<>(),
                uniqueID,
                id,
                name
        );
    }

    public LightUniverse(List<Light> lights, List<Ring> rings, UUID uniqueID, int id, String name) {
        this.lights = lights;
        this.rings = rings;
        this.uniqueID = uniqueID;
        this.id = id;
        if (name.equals("Unnamed-Universe")) {
            name += "-" + id;
        }
        this.name = name;
    }

    public void load() {
        this.lights.forEach(light -> {
            light.start();
            light.load();
            light.on(new Color(0x0066ff));
        });
        this.rings.forEach(Ring::buildLasers);
        this.rings.forEach(Ring::start);
        isLoaded = true;
    }

    public void unload() {
        this.lights.forEach(light -> {
            light.unload();
            light.off(new Color(0x000000));
            light.stop();
        });
        this.rings.forEach(Ring::stop);
        this.rings.forEach(Ring::unload);
        isLoaded = false;
    }

    public @Nullable Light getLight(UUID uuid) {
        return lights
                .stream()
                .filter(light -> uuid.equals(light.getUniqueID()))
                .findFirst()
                .orElse(null);
    }

    public @Nullable Light getLight(String name) {
        return lights
                .stream()
                .filter(light -> name.equals(light.getName()))
                .findFirst()
                .orElse(null);
    }

    public void addLight(Light light) {
        this.lights.add(light);
    }

    public void removeLight(Light light) {
        this.lights.remove(light);
    }

    public void addRing(Ring ring) {
        this.rings.add(ring);
    }

    public void removeRing(Ring ring) {
        this.rings.remove(ring);
    }

    public @Nullable Ring getRing(String name) {
        return rings
                .stream()
                .filter(light -> name.equals(light.getName()))
                .findFirst()
                .orElse(null);
    }

    public List<GameObject> getGameObjects() {
        List<GameObject> gameObjects = new ArrayList<>();
        gameObjects.addAll(lights);
        gameObjects.addAll(rings);
        return gameObjects;
    }

    public @Nullable GameObject getGameObject(String id, EnvironmentObject.LookupMethod lookupMethod) {
        var objects = getGameObjects();
        for (GameObject object : objects) {
            switch(lookupMethod) {
                case EXACT -> {
                    if (object.name().equals(id)) {
                        Nightclub.getChameleon().getLogger().info("Found object: " + object.name() + " with " + lookupMethod.name() + " " + id);
                        return object;
                    }
                }
                case CONTAINS -> {
                    if (object.name().contains(id)) {
                        Nightclub.getChameleon().getLogger().info("Found object: " + object.name() + " with " + lookupMethod.name() + " " + id);
                        return object;
                    }
                }
                case REGEX -> {
                    if (object.name().matches(id)) {
                        Nightclub.getChameleon().getLogger().info("Found object: " + object.name() + " with " + lookupMethod.name() + " " + id);
                        return object;
                    }
                }
                default -> {
                    return null;
                }
            }
        }
        return null;
    }

    public static class LightUniverseInstanceCreator implements InstanceCreator<LightUniverse> {
        public LightUniverse createInstance(Type type) {
            return new LightUniverse(new ArrayList<>(), UUID.randomUUID(), 0, "LightUniverseInstanceCreator");
        }
    }

}

