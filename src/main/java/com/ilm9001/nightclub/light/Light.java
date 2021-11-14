package com.ilm9001.nightclub.light;

import com.google.gson.InstanceCreator;
import com.ilm9001.nightclub.laser.LaserWrapper;
import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightSpeedChannel;
import com.ilm9001.nightclub.light.pattern.LightPattern;
import com.ilm9001.nightclub.util.Location;
import lombok.*;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ToString
@EqualsAndHashCode
public class Light {
    private static final transient ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final transient int DELAY = 100; // run every x ms
    // annotations lol
    @Getter private final UUID uniqueID;
    @Getter @Setter private String name;
    @Getter private Location location;
    @Getter private LightPattern pattern;
    @Getter private LightType type;
    @Getter private LightChannel channel;
    @Getter private LightSpeedChannel speedChannel;
    @Getter private double maxLength;
    @Getter @Setter private double onLength; // 0 to 100, percentage of maxLength
    @Getter private double speed;
    @Getter private double patternSizeMultiplier;
    @Getter @Setter private int timeToFadeToBlack; // x * 100 ms
    @Getter private int lightCount;
    @Getter private boolean flipStartAndEnd; // flipped start and end makes downward pointing beams brighter, upward pointing beams less bright
    
    private final transient List<LaserWrapper> lasers = new ArrayList<>();
    @Getter @Setter private transient double length = 0; // 0 to 100, percentage of maxLength.
    @Getter @Setter private transient double x = 0; // 0 to 100, usually percentage of 360
    private transient boolean isOn = false;
    @Getter @Setter private transient double multipliedSpeed; // speed, but when internally multiplied by events
    @Getter @Setter private transient int timeToFade; // internal fade off value
    private final transient Runnable run;
    private transient Thread thread;
    
    public Light(Location loc, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc, "Unnamed-Light" + new Random().nextInt(), pattern, type, channel);
    }
    
    public Light(Location loc, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc, UUID.randomUUID(), name, pattern, type, channel);
    }
    
    public Light(Location loc, UUID uniqueID, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(uniqueID, name, loc, 0, 0, 0, 0, 0, 0, false, pattern, type, channel, LightSpeedChannel.DEFAULT);
    }
    
    @Builder
    public Light(UUID uuid, String name, Location location, double maxLength, double onLength, double speed, double patternSizeMultiplier, int timeToFadeToBlack, int lightCount,
                 boolean flipStartAndEnd, LightPattern pattern, LightType type, LightChannel channel, LightSpeedChannel speedChannel) {
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
        this.speedChannel = speedChannel;
        this.flipStartAndEnd = flipStartAndEnd;
        this.patternSizeMultiplier = patternSizeMultiplier;
        
        load();
        
        if (flipStartAndEnd) {
            lasers.forEach((laser) -> laser.setEnd(this.location));
        } else {
            lasers.forEach((laser) -> laser.setStart(this.location));
        }
        
        run = () -> {
            if (timeToFade > 0 && length > 0) {
                timeToFade--;
                length -= 100.0 / this.timeToFadeToBlack;
            }
            if (length <= 0) {
                off();
                timeToFade = 0;
                length = 0.1;
            }
            if (length > 100) {
                length = 100.0;
            }
            x = (x + multipliedSpeed) % 100;
            
            for (int i = 0; i < lasers.size(); i++) {
                LaserWrapper laser = lasers.get(i);
                /*
                Here we make a ray the size of (length) from the location of this Light, then we add a 2d plane to it (which is where our pattern is) with a
                x value that is seperated evenly for each laser.
                 */
                double seperated = x + (100.0 / lasers.size()) * i;
                if (this.pattern == LightPattern.LINE) {
                    seperated = x + (50.0 / lasers.size()) * i;
                }
                Vector3D v = new Vector3D(Math.toRadians(this.location.getYaw()), Math.toRadians(this.location.getPitch())).normalize().scalarMultiply(getMaxLengthPercent());
                Rotation r = new Rotation(v, this.location.getRotation(), RotationConvention.FRAME_TRANSFORM);
                Vector3D v2 = this.pattern.getPattern().apply(v, seperated, r, this.patternSizeMultiplier * (length / 100));
                Vector3D v3 = v.add(v2);
                
                if (this.flipStartAndEnd) {
                    laser.setStart(this.location.clone().add(v3.getX(), v3.getZ(), v3.getY()));
                } else {
                    laser.setEnd(this.location.clone().add(v3.getX(), v3.getZ(), v3.getY()));
                }
            }
        };
    }
    
    public void load() {
        this.channel.getHandler().removeListener(this);
        this.speedChannel.getChannel().getHandler().removeSpeedListener(this);
        this.channel.getHandler().addListener(this);
        this.speedChannel.getChannel().getHandler().addSpeedListener(this);
        this.multipliedSpeed = speed;
        buildLasers();
    }
    public void unload() {
        this.channel.getHandler().removeListener(this);
        this.speedChannel.getChannel().getHandler().removeSpeedListener(this);
        off();
        stop();
    }
    /**
     * Starts the movement runnable of this Light. This Light will be completely stationary if it is not started before being turned on.
     */
    public void start() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(run);
            executorService.scheduleAtFixedRate(thread, 1, DELAY, TimeUnit.MILLISECONDS);
        }
    }
    /**
     * Stops (interrupts) the movement runnable of this Light.
     */
    public void stop() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
    
    /**
     * Stops all current LaserWrapper's and (re-)builds them. Use when changing pattern, pitch, yaw, location, LightType, lightCount.
     * If you want to turn them back on, call on()
     */
    public void buildLasers() {
        for (LaserWrapper lsr : lasers) {
            lsr.stop();
        }
        isOn = false;
        lasers.clear();
        for (int i = 0; i < lightCount; i++) {
            LaserWrapper laser;
            double seperated = 0 + (100.0 / lightCount) * i;
            if (pattern == LightPattern.LINE) {
                seperated = 0 + (50.0 / lightCount) * i;
            }
            Vector3D v = new Vector3D(Math.toRadians(this.location.getYaw()), Math.toRadians(this.location.getPitch())).normalize().scalarMultiply(maxLength * onLength / 100.0);
            Rotation r = new Rotation(v, this.location.getRotation(), RotationConvention.FRAME_TRANSFORM);
            Vector3D v2 = this.pattern.getPattern().apply(v, seperated, r, this.patternSizeMultiplier * (onLength / 100));
            Vector3D v3 = v.add(v2);
            if (flipStartAndEnd) {
                laser = new LaserWrapper(location.clone().add(v3.getX(), v3.getZ(), v3.getY()), location, -1, 256, type);
            } else {
                laser = new LaserWrapper(location, location.clone().add(v3.getX(), v3.getZ(), v3.getY()), -1, 256, type);
            }
            lasers.add(laser);
        }
    }
    /**
     * Turns Light on, sets length to onLength and sets timeToFade to 0
     */
    public void on(Color color) {
        lasers.forEach(LaserWrapper::start);
        if (length < onLength && !isOn) {
            length = onLength;
        }
        length = onLength * (color.getAlpha() / 255.0);
        isOn = true;
        timeToFade = 0;
    }
    /**
     * Turns Light off, sets length to 0.1 and sets timeToFade to 0
     */
    public void off() {
        lasers.forEach(LaserWrapper::stop);
        isOn = false;
        length = 0.1;
        timeToFade = 0;
    }
    /**
     * Flashes light in a similar way to beat saber, simulating brightness with a longer beam
     */
    public void flash(Color color) {
        if (isOn) {
            length = onLength * (color.getAlpha() / 255.0);
            length += (100 - onLength) / 3;
            timeToFade += 3;
            lasers.forEach(LaserWrapper::changeColor);
        } else {
            flashOff(color);
        }
    }
    /**
     * Flashes light in a similar way to beat saber, simulating brightness with a longer beam and then fades to black
     */
    public void flashOff(Color color) {
        on(color);
        flash(color);
        timeToFade = timeToFadeToBlack;
    }
    /**
     * Set which LightChannel this Light should be listening to.
     *
     * @param channel LightChannel to listen to
     */
    public void setChannel(LightChannel channel) {
        this.channel.getHandler().removeListener(this);
        channel.getHandler().addListener(this);
        this.channel = channel;
    }
    
    /**
     *
     */
    public void setSpeedChannel(LightSpeedChannel speedChannel) {
        this.speedChannel.getChannel().getHandler().removeSpeedListener(this);
        speedChannel.getChannel().getHandler().addSpeedListener(this);
        this.speedChannel = speedChannel;
    }
    
    /**
     * Set speed before it is internally multiplied by LightEvents.
     *
     * @param speed Base Speed before multiplier
     */
    public void setBaseSpeed(double speed) {
        this.speed = speed;
    }
    /**
     * Set speed with LightEvent-specified multiplier
     */
    public void setSpeed(double multiplier) {
        if (this.multipliedSpeed == speed * multiplier) {
            x = (x + 12) % 100;
        } // laser "reset"
        this.multipliedSpeed = speed * multiplier;
    }
    
    private double getMaxLengthPercent() {
        return maxLength * length / 100.0;
    }
    /**
     * Set new LightPattern to use
     *
     * @param pattern LightPattern to use
     */
    public void setPattern(LightPattern pattern) {
        if (pattern != this.pattern) {
            this.pattern = pattern;
            buildLasers();
        }
    }
    public void setMaxLength(double maxLength) {
        this.maxLength = maxLength;
        buildLasers();
    }
    public void setType(LightType type) {
        this.type = type;
        buildLasers();
    }
    public void setPatternSizeMultiplier(double patternSizeMultiplier) {
        this.patternSizeMultiplier = patternSizeMultiplier;
        buildLasers();
    }
    public void setLocation(Location location) {
        this.location = location;
        buildLasers();
    }
    public void setFlipStartAndEnd(boolean flipStartAndEnd) {
        this.flipStartAndEnd = flipStartAndEnd;
        buildLasers();
    }
    public void setLightCount(int lightCount) {
        this.lightCount = lightCount;
        buildLasers();
    }
    public void setRotation(double rotation) {
        this.location.setRotation(rotation);
        buildLasers();
    }
    public static class LightUniverseInstanceCreator implements InstanceCreator<Light> {
        public Light createInstance(Type type) {
            return new Light(new Location(0, 0, 0, 0, 0), LightPattern.STILL, LightType.GUARDIAN_BEAM, LightChannel.CENTER_LIGHTS);
        }
    }
}
