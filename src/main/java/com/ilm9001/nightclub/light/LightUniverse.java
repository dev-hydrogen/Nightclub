package com.ilm9001.nightclub.light;

import com.google.gson.InstanceCreator;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.Location;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString
public class LightUniverse {
    @Getter private UUID uniqueID;
    @Getter private int id;
    @Getter @Setter private String name;
    @Getter private final List<Light> lights;
    
    public LightUniverse() {
        // pain
        this(
                new ArrayList<>(),
                UUID.randomUUID(),
                Nightclub.getInstance().getJSONreader().getLastUniverse().getId()+1,
                "Unnamed-Universe"
        );
    }
    
    public LightUniverse(List<Light> lights, UUID uniqueID, int id, String name) {
        this.lights = lights;
        this.uniqueID = uniqueID;
        this.id = id;
        if(name.equals("Unnamed-Universe")) { name+="-"+id;}
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
    
    public void removeLight(Light light) {this.lights.remove(light);}
    
    public void updateLight(Light light) {
    
    }
    
    public static class LightUniverseInstanceCreator implements InstanceCreator<LightUniverse> {
        public LightUniverse createInstance(Type type) {
            return new LightUniverse(new ArrayList<>(),UUID.randomUUID(),0,"LightUniverseInstanceCreator");
        }
    }
}

