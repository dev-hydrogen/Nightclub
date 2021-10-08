package com.ilm9001.nightclub.light;

import com.ilm9001.nightclub.laser.LaserWrapper;
import com.ilm9001.nightclub.light.pattern.LightPattern;
import com.ilm9001.nightclub.util.Location;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Data
public class Light {
    private static final transient ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final transient int DELAY = 100; // run every x ms
    private UUID uniqueID;
    private String name;
    private Location location;
    private LightPattern pattern;
    private LightType type;
    private LightChannel channel;
    private double maxLength;
    private double onLength; // 0 to 100, percentage of maxLength
    private double speed;
    private double patternSizeMultiplier;
    private int timeToFadeToBlack; // x * 100 ms
    private int lightCount;
    private boolean flipStartAndEnd; // flipped start and end makes downward pointing beams brighter, upward pointing beams less bright
    @Builder.Default
    private transient List<LaserWrapper> lasers = new ArrayList<>();
    @Builder.Default
    private transient double length = 0; // 0 to 100, percentage of maxLength.
    @Builder.Default
    private transient double x = 0; // 0 to 100, usually percentage of 360
    @Builder.Default
    private transient boolean isOn = false;
    @Builder.Default
    private transient double multipliedSpeed = speed; // speed, but when internally multiplied by events
    private transient int timeToFade; // internal fade off value
    private transient Runnable run;
    private transient Thread thread;
    
    public Light(Location loc, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc, "Unnamed-Light", pattern, type, channel);
    }
    
    public Light(Location loc, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc, UUID.randomUUID(), name, pattern, type, channel);
    }
    
    public Light(Location loc, UUID uniqueID, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(uniqueID, name, loc, 0, 0, 0, 0, 0, 0, false, pattern, type, channel);
    }
    
    @Builder
    public Light(UUID uuid, String name, Location location, double maxLength, double onLength, double speed, double patternSizeMultiplier, int timeToFadeToBlack, int lightCount,
                 boolean flipStartAndEnd, LightPattern pattern, LightType type, LightChannel channel) {
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
        this.flipStartAndEnd = flipStartAndEnd;
        this.patternSizeMultiplier = patternSizeMultiplier;
        
        multipliedSpeed = speed;
        
        buildLasers();
        
        if (flipStartAndEnd || this.type == LightType.END_CRYSTAL_BEAM) {
            lasers.forEach((laser) -> laser.setEnd(location));
        } else {
            lasers.forEach((laser) -> laser.setStart(location));
        }
        
        run = () -> {
            try {
                if (timeToFade > 0 && length > 0) {
                    timeToFade--;
                    length -= 100.0 / timeToFadeToBlack;
                }
                if (length < 0) {
                    off();
                    timeToFade = 0;
                    length = 0;
                }
                x = (x + multipliedSpeed) % 100;
                length %= 100;
                for (int i = 0; i < lasers.size(); i++) {
                    LaserWrapper laser = lasers.get(i);
                    /*
                    Here we make a ray the size of length from the location of this Light, then we add a 2d plane to it (which is where our pattern is) with a
                    x value that is seperated evenly for each laser.
                     */
                    Vector3D v = new Vector3D(Math.toRadians(this.location.getYaw()), Math.toRadians(this.location.getPitch())).normalize().scalarMultiply(getMaxLengthPercent());
                    Vector3D v2 = this.pattern.getPattern().apply(v, x + (100.0 / lasers.size()) * i).scalarMultiply(this.patternSizeMultiplier);
                    Vector3D v3 = v.add(v2);
                    
                    if (flipStartAndEnd || this.type == LightType.END_CRYSTAL_BEAM) {
                        laser.setStart(this.location.clone().add(v3.getX(), v3.getZ(), v3.getY()));
                    } else {
                        laser.setEnd(this.location.clone().add(v3.getX(), v3.getZ(), v3.getY()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        thread = new Thread(run);
    }
    
    public void start() {
        if (!thread.isAlive()) {
            thread = new Thread(run);
            executorService.scheduleAtFixedRate(thread, 1, DELAY, TimeUnit.MILLISECONDS);
        }
    }
    public void stop() {
        if (thread.isAlive()) {
            thread.interrupt();
        }
    }
    
    public void buildLasers() {
        for (LaserWrapper lsr : lasers) {
            lsr.stop();
        }
        for (int i = 0; i < lightCount; i++) {
            LaserWrapper laser = new LaserWrapper(location, location, -1, 128, type.getType());
            lasers.add(laser);
        }
    }
    
    public void on() {
        isOn = true;
        length = onLength;
        for (LaserWrapper lsr : lasers) {
            lsr.start();
        }
    }
    public void off() {
        isOn = false;
        length = 0;
        for (LaserWrapper lsr : lasers) {
            lsr.stop();
        }
    }
    public void flash() {
        if (isOn) {
            on();
            length += onLength / 100.0 * 5;
            timeToFade = 2;
        } else {
            flash();
        }
    }
    public void flashOff() {
        on();
        timeToFade = timeToFadeToBlack;
    }
    
    public void setSpeed(double multiplier) {
        this.multipliedSpeed = speed * multiplier;
    }
    
    private double getMaxLengthPercent() {
        return maxLength * length / 100.0;
    }
}
