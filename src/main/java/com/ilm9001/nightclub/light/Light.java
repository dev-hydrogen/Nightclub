package com.ilm9001.nightclub.light;

import com.google.gson.InstanceCreator;
import com.ilm9001.nightclub.laser.LaserWrapper;
import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightSpeedChannel;
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
public class Light implements LightI {
    private transient ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final transient int DELAY = 100; // run every x ms
    // annotations lol
    @Getter private final UUID uniqueID;
    @Getter @Setter private String name;
    @Getter @Setter private Location location;
    @Getter @Setter private LightData data;
    @Getter private LightType type;
    @Getter private LightChannel channel;
    @Getter private LightSpeedChannel speedChannel;
    
    private final transient List<LaserWrapper> lasers = new ArrayList<>();
    @Getter @Setter private transient double length = 0; // 0 to 100, percentage of maxLength.
    @Getter @Setter private transient double x = 0; // 0 to 100, usually percentage of 360
    @Getter @Setter private transient double x2 = 0; // 0 to 100, usually percentage of 360, secondary pattern
    private transient boolean isOn = false;
    @Getter @Setter private transient double multipliedSpeed; // speed, but when internally multiplied by events
    @Getter @Setter private transient double secondaryMultipliedSpeed;
    @Getter @Setter private transient int timeToFade; // internal fade off value
    private final transient Runnable run;
    private transient boolean isLoaded;
    
    public Light(Location loc, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc, "Unnamed-Light" + new Random().nextInt(), pattern, type, channel);
    }
    
    public Light(Location loc, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc, UUID.randomUUID(), name, pattern, type, channel);
    }
    
    public Light(Location loc, UUID uniqueID, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(uniqueID, name, loc, type, channel, LightSpeedChannel.DEFAULT, new LightData(
                new LightPatternData(pattern, 0, 0, 0), new LightPatternData(LightPattern.STILL,
                0, 0, 0), 0, 0,
                0, 0, false));
    }
    
    @Builder
    public Light(UUID uuid, String name, Location location, LightType type, LightChannel channel, LightSpeedChannel speedChannel, LightData data) {
        this.uniqueID = uuid;
        this.name = name;
        this.location = location;
        this.type = type;
        this.channel = channel;
        this.speedChannel = speedChannel;
        this.data = data;
        
        load();
        
        if (this.data.isFlipStartAndEnd()) {
            lasers.forEach((laser) -> laser.setEnd(this.location));
        } else {
            lasers.forEach((laser) -> laser.setStart(this.location));
        }
        
        run = () -> {
            if (timeToFade > 0 && length > 0) {
                timeToFade--;
                length -= 100.0 / this.data.getTimeToFadeToBlack();
            }
            if (length <= 0) {
                off(new Color(0x000000));
                timeToFade = 0;
                length = 0.1;
            }
            if (length > 100) {
                length = 100.0;
            }
            x = (x + multipliedSpeed) % 100;
            x2 = (x2 + secondaryMultipliedSpeed) % 100;
            
            for (int i = 0; i < lasers.size(); i++) {
                LaserWrapper laser = lasers.get(i);
                /*
                Here we make a ray the size of (length) from the location of this Light, then we add a 2d plane to it (which is where our pattern is) with an
                x value that is separated evenly for each laser. This pattern is then moved (as a whole) by the second pattern.
                 */
                double separated = x + (100.0 / lasers.size()) * i;
                Vector3D v = new Vector3D(Math.toRadians(this.location.getYaw()), Math.toRadians(this.location.getPitch())).normalize().scalarMultiply(getMaxLengthPercent());
                Rotation r = new Rotation(v, this.data.getPatternData().getRotation(), RotationConvention.FRAME_TRANSFORM);
                Rotation r2 = new Rotation(v, this.data.getSecondPatternData().getRotation(), RotationConvention.FRAME_TRANSFORM);
                Vector3D v2 = this.data.getPatternData().getPattern().apply(v, separated, r, this.data.getPatternData().getPatternSizeMultiplier() * (length / 100));
                Vector3D v3 = this.data.getSecondPatternData().getPattern().apply(v, x2, r2, this.data.getSecondPatternData().getPatternSizeMultiplier() * (length / 100));
                Vector3D v4 = v.add(v3).add(v2);
                
                if (this.data.isFlipStartAndEnd()) {
                    laser.setStart(this.location.clone().add(v4.getX(), v4.getZ(), v4.getY()));
                } else {
                    laser.setEnd(this.location.clone().add(v4.getX(), v4.getZ(), v4.getY()));
                }
            }
        };
    }
    
    public void load() {
        this.channel.removeListener(this);
        this.speedChannel.getChannel().removeSpeedListener(this);
        this.channel.addListener(this);
        this.speedChannel.getChannel().addSpeedListener(this);
        this.multipliedSpeed = data.getPatternData().getSpeed();
        this.secondaryMultipliedSpeed = data.getSecondPatternData().getSpeed();
        buildLasers();
        isLoaded = true;
    }
    public void unload() {
        this.channel.removeListener(this);
        this.speedChannel.getChannel().removeSpeedListener(this);
        off(new Color(0x000000));
        stop();
        isLoaded = false;
    }
    /**
     * Starts the movement runnable of this Light. This Light will be completely stationary if it is not started before being turned on.
     */
    public void start() {
        stop();
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(run, 1, DELAY, TimeUnit.MILLISECONDS);
    }
    /**
     * Stops the movement runnable of this Light.
     */
    public void stop() {
        executorService.shutdownNow();
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
        for (int i = 0; i < data.getLightCount(); i++) {
            LaserWrapper laser;
            double separated = 0 + (100.0 / data.getLightCount()) * i;
            Vector3D v = new Vector3D(Math.toRadians(this.location.getYaw()), Math.toRadians(this.location.getPitch())).normalize().scalarMultiply(data.getMaxLength() * data.getOnLength() / 100.0);
            Rotation r = new Rotation(v, this.data.getPatternData().getRotation(), RotationConvention.FRAME_TRANSFORM);
            Rotation r2 = new Rotation(v, this.data.getSecondPatternData().getRotation(), RotationConvention.FRAME_TRANSFORM);
            Vector3D v2 = data.getPatternData().getPattern().apply(v, separated, r, data.getPatternData().getPatternSizeMultiplier() * (data.getOnLength() / 100));
            Vector3D v3 = data.getSecondPatternData().getPattern().apply(v, 0.0, r2, data.getSecondPatternData().getPatternSizeMultiplier() * (data.getOnLength() / 100));
            Vector3D v4 = v.add(v3).add(v2);
            if (data.isFlipStartAndEnd()) {
                laser = new LaserWrapper(location.clone().add(v4.getX(), v4.getZ(), v4.getY()), location, -1, 256, type);
            } else {
                laser = new LaserWrapper(location, location.clone().add(v4.getX(), v4.getZ(), v4.getY()), -1, 256, type);
            }
            lasers.add(laser);
        }
    }
    /**
     * Turns Light on, sets length to onLength and sets timeToFade to 0
     */
    public void on(Color color) {
        if (!isLoaded) return;
        lasers.forEach(LaserWrapper::start);
        if (length < data.getOnLength() && !isOn) {
            length = data.getOnLength();
        }
        length = data.getOnLength() * (color.getAlpha() / 255.0);
        isOn = true;
        timeToFade = 0;
    }
    /**
     * Turns Light off, sets length to 0.1 and sets timeToFade to 0
     */
    public void off(Color color) {
        if (!isLoaded) return;
        lasers.forEach(LaserWrapper::stop);
        isOn = false;
        length = 0.1;
        timeToFade = 0;
    }
    /**
     * Flashes light in a similar way to beat saber, simulating brightness with a longer beam
     */
    public void flash(Color color) {
        if (!isLoaded) return;
        if (isOn) {
            length = data.getOnLength() * (color.getAlpha() / 255.0);
            length += (100 - data.getOnLength()) / 3;
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
        if (!isLoaded) return;
        on(color);
        flash(color);
        timeToFade = data.getTimeToFadeToBlack();
    }
    /**
     * Set which LightChannel this Light should be listening to.
     *
     * @param channel LightChannel to listen to
     */
    public void setChannel(LightChannel channel) {
        this.channel.removeListener(this);
        channel.addListener(this);
        this.channel = channel;
    }
    
    /**
     *
     */
    public void setSpeedChannel(LightSpeedChannel speedChannel) {
        this.speedChannel.getChannel().removeSpeedListener(this);
        speedChannel.getChannel().addSpeedListener(this);
        this.speedChannel = speedChannel;
    }
    
    /**
     * Set speed before it is internally multiplied by LightEvents.
     *
     * @param speed Base Speed before multiplier
     */
    public void setBaseSpeed(double speed) {
        data.getPatternData().setSpeed(speed);
        multipliedSpeed = speed;
    }
    public void setSecondaryBaseSpeed(double speed) {
        data.getSecondPatternData().setSpeed(speed);
        secondaryMultipliedSpeed = speed;
    }
    
    /**
     * Set speed with LightEvent-specified multiplier
     */
    public void setSpeed(double multiplier) {
        if (!isLoaded) return;
        if (multipliedSpeed == 0 && multiplier > 0) {
            double random = new Random().nextDouble() * 100;
            x += random;
            x2 += random;
        }
        if (this.multipliedSpeed == data.getPatternData().getSpeed() * multiplier) { // laser "reset"
            x = (x + 12) % 100;
            x2 = (x2 + 12) % 100;
        }
        if (multiplier == 0) {
            x = 100.0 / data.getLightCount();
            x2 = 100.0 / data.getLightCount();
        }
        this.multipliedSpeed = data.getPatternData().getSpeed() * multiplier;
        this.secondaryMultipliedSpeed = data.getSecondPatternData().getSpeed() * multiplier;
        if (multipliedSpeed >= 40.0) {
            multipliedSpeed = 40.0;
            if (secondaryMultipliedSpeed >= 40.0) {
                secondaryMultipliedSpeed = 40.0;
            }
        }
    }
    
    private double getMaxLengthPercent() {
        return data.getMaxLength() * length / 100.0;
    }
    
    public void setType(LightType type) {
        this.type = type;
        buildLasers();
    }
    
    public static class LightUniverseInstanceCreator implements InstanceCreator<Light> {
        public Light createInstance(Type type) {
            return new Light(new Location(0, 0, 0, 0, 0), LightPattern.STILL, LightType.GUARDIAN_BEAM, LightChannel.CENTER_LIGHTS);
        }
    }
}
