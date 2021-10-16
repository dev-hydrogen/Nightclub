package com.ilm9001.nightclub.light;

import com.google.gson.InstanceCreator;
import com.ilm9001.nightclub.Nightclub;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString
public class LightUniverse {
    @Getter private final UUID uniqueID;
    @Getter private final int id;
    @Getter @Setter private String name;
    @Getter private final List<Light> lights;
    @Getter @Setter World world;
    
    public LightUniverse() {
        // pain
        this(
                new ArrayList<>(),
                UUID.randomUUID(),
                Nightclub.getJSONreader().getLastUniverse().getId() + 1,
                "Unnamed-Universe",
                Bukkit.getWorlds().get(0)
        );
    }
    
    public LightUniverse(List<Light> lights, UUID uniqueID, int id, String name, World world) {
        this.lights = lights;
        this.uniqueID = uniqueID;
        this.id = id;
        this.world = world;
        if (name.equals("Unnamed-Universe")) {
            name += "-" + id;
        }
        this.name = name;
    }
    
    public Light getLight(UUID uuid) {
        return lights
                .stream()
                .filter(light -> uuid.equals(light.getUniqueID()))
                .findFirst()
                .orElse(null);
    }
    
    public void addLight(Light light) {
        this.lights.add(light);
    }
    
    public void removeLight(Light light) {
        this.lights.remove(light);
    }
    
    public void updateLight(Light light) {
    
    }
    
    public static class LightUniverseInstanceCreator implements InstanceCreator<LightUniverse> {
        public LightUniverse createInstance(Type type) {
            return new LightUniverse(new ArrayList<>(), UUID.randomUUID(), 0, "LightUniverseInstanceCreator", Bukkit.getWorlds().get(0));
        }
    }
}

