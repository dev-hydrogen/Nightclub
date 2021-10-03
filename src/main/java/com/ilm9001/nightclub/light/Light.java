package com.ilm9001.nightclub.light;

import com.ilm9001.nightclub.light.pattern.LightPattern;
import com.ilm9001.nightclub.util.Location;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
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
    private transient double length; // 0 to 100, percentage of maxLength.
    private transient double x; // 0 to 100, usually percentage of 360
    private transient double multipliedSpeed; // speed, but when internally multiplied by events
    
    public Light(Location loc, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc,"Unnamed-Light",pattern,type,channel);
    }
    public Light(Location loc, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc,UUID.randomUUID(),name,pattern,type,channel);
    }
    public Light(Location loc, UUID uniqueID, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this.location = loc;
        this.uniqueID = uniqueID;
        this.name = name;
        this.pattern = pattern;
        this.type = type;
        this.channel = channel;
    }
    
}
