package com.ilm9001.nightclub.light;

import com.ilm9001.nightclub.laser.LaserWrapper;
import com.ilm9001.nightclub.light.pattern.LightPattern;
import com.ilm9001.nightclub.util.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Light {
    private UUID uniqueID;
    private String name;
    private Location location;
    private double maxLength;
    private double onLength; // 0 to 100, percentage of maxLength
    private double speed;
    private int timeToFadeToBlack;
    private int lightCount;
    private LightPattern pattern;
    private LightType type;
    private LightChannel channel;
    private transient List<LaserWrapper> lasers = new ArrayList<>();
    private transient double length = 0; // 0 to 100, percentage of maxLength.
    private transient double x = 0; // 0 to 100, usually percentage of 360
    private transient double multipliedSpeed = 3; // speed, but when internally multiplied by events
    
    public Light(Location loc, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc,"Unnamed-Light",pattern,type,channel);
    }
    public Light(Location loc, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc,UUID.randomUUID(),name,pattern,type,channel);
    }
    public Light(Location loc, UUID uniqueID, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(uniqueID,name,loc,0,0,0,0,0,pattern,type,channel);
    }
    public Light(UUID uuid, String name, Location location, double maxLength, double onLength, double speed, int timeToFadeToBlack, int lightCount,
    LightPattern pattern, LightType type, LightChannel channel) {
        this.uniqueID = uuid;
        this.name = name;
        this.location = location;
        this.maxLength = maxLength;
        this.onLength = onLength;
        this.speed = speed;
        this.timeToFadeToBlack = timeToFadeToBlack;
        this.lightCount = lightCount;
        this.pattern = pattern;
        this.type = type;
        this.channel = channel;
    }
    
}
