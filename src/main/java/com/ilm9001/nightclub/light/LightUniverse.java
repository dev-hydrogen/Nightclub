package com.ilm9001.nightclub.light;

import com.google.gson.InstanceCreator;
import com.ilm9001.nightclub.Nightclub;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
        this.lights = lights;
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
        isLoaded = true;
    }
    public void unload() {
        this.lights.forEach(light -> {
            light.unload();
            light.off(new Color(0x000000));
            light.stop();
        });
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
    
    public static class LightUniverseInstanceCreator implements InstanceCreator<LightUniverse> {
        public LightUniverse createInstance(Type type) {
            return new LightUniverse(new ArrayList<>(), UUID.randomUUID(), 0, "LightUniverseInstanceCreator");
        }
    }
}

