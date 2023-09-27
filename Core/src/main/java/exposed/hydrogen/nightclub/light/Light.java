package exposed.hydrogen.nightclub.light;

import com.google.gson.JsonArray;
import exposed.hydrogen.nightclub.GameObject;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.beatmap.json.GradientEvent;
import exposed.hydrogen.nightclub.light.data.*;
import exposed.hydrogen.nightclub.light.event.LightChannel;
import exposed.hydrogen.nightclub.light.event.LightSpeedChannel;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.DebugMarkerWrapper;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import lombok.*;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@EqualsAndHashCode
public class Light implements GameObject, Cloneable {
    private transient ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final int DELAY = 100; // run every x ms
    // annotations lol
    @Getter private final UUID uniqueID;
    @Getter @Setter private String name;
    @Getter @Setter private Location location; // only changes when manually set
    @Getter @Setter private LightData data;
    @Getter private LightType type;
    @Getter private LightChannel channel;
    @Getter private LightSpeedChannel speedChannel;
    @Getter private transient DebugMarkerWrapper marker;

    private final transient List<LaserWrapper> lasers = new LinkedList<>();
    private transient GradientEvent currentGradient;
    private transient Long startTime;
    private transient double length = 0; // 0 to 100, percentage of maxLength.
    private transient double lastLength = 0;
    private transient double zoomTime = 0; // 0 to 1, current zoom time. does nothing if <=0
    private transient Location loc; // current location
    private transient Location vec3Scale = new Location(); // vec3 scale
    private transient Location vec3Rot = new Location(); // vec3 rotation
    @Getter @Setter private transient double x = 0; // 0 to 100, usually percentage of 360
    @Getter @Setter private transient double x2 = 0; // 0 to 100, usually percentage of 360, secondary pattern
    @Getter private transient Color color; // current color
    private transient int duration; // duration to next event
    private transient boolean isOn = false;
    private transient boolean isZoomed = false;
    private transient double multipliedSpeed; // speed, but when internally multiplied by events
    private transient double secondaryMultipliedSpeed;
    private transient int timeToFade; // internal fade off value
    private final transient Runnable run;
    private transient boolean isLoaded;
    private transient boolean isEnabled = true;

    private Light() {
        this(new Location(0, 0, 0, 0, 0), LightPattern.STILL, LightType.GUARDIAN_BEAM, LightChannel.CENTER_LIGHTS);
    }

    public Light(Location loc, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc, "Unnamed-Light" + new Random().nextInt(), pattern, type, channel);
    }

    public Light(Location loc, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(loc, UUID.randomUUID(), name, pattern, type, channel);
    }

    public Light(Location loc, UUID uniqueID, String name, LightPattern pattern, LightType type, LightChannel channel) {
        this(uniqueID, name, loc, type, channel, LightSpeedChannel.DEFAULT, new LightData(
                new LightPatternData(pattern, 0, 0, 0, 0), new LightPatternData(LightPattern.STILL,
                0, 0, 0, 0), new ArrayList<>(), new RingMovementData(new Location(), 0, 0), 0, 0, 0,
                0, false,false));
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
        color = new Color(0x000000);

        run = () -> {
            try {
                if(!isEnabled) return;
                if (isZoomed ? zoomTime < 1 : zoomTime > 0) {
                    double duration = getData().getRingMovementData().getDuration();
                    zoomTime = isZoomed ?
                            zoomTime + duration / 1000 / (DELAY / 10.0)
                            : zoomTime - duration / 1000 / (DELAY / 10.0);
                }
                if(zoomTime < 0 && !isZoomed) {
                    zoomTime = 0;
                }
                if(zoomTime > 1 && isZoomed) {
                    zoomTime = 1;
                }
                if (timeToFade > 0 && length > 0) {
                    timeToFade--;
                    length -= 100.0 / this.data.getTimeToFadeToBlack();
                }
                if (length > 100) {
                    length = 100.0;
                }
                if(multipliedSpeed == 0 && secondaryMultipliedSpeed == 0 && lastLength == length &&
                        (isZoomed ? zoomTime >= 1 : zoomTime <= 0)) {
                    // do nothing if no movement or zooming needs to be happening
                    return;
                }
                if(currentGradient != null && length > 0.1) {
                    long gradientEndMillis = startTime + currentGradient.getEndTime();
                    long gradientStartMillis = startTime + currentGradient.getStartTime();
                    if(System.currentTimeMillis() < gradientEndMillis && System.currentTimeMillis() > gradientStartMillis) {
                        long fraction = System.currentTimeMillis() / gradientEndMillis;
                        color = currentGradient.getLerpType().lerp(
                                currentGradient.getStartColor(),
                                currentGradient.getEndColor(),
                                fraction,
                                currentGradient.getEasing());
                        marker.setColor(color);
                        marker.setDuration(DELAY+20);
                        marker.start(256);
                    }
                    if(System.currentTimeMillis() > gradientEndMillis) {
                        currentGradient = null;
                    }
                }
                x = (x + multipliedSpeed) % 100;
                x2 = (x2 + secondaryMultipliedSpeed) % 100;
                // do chroma gradient
                if (length < 0.1) {
                    off(new Color(0x000000));
                    timeToFade = 0;
                    length = 0.1;
                }
                lastLength = length;
                double l = (length + this.data.getOnLength() * (color.getAlpha() / 255.0))/2;

                // a (invisible) "ray", pointing towards the set pitch and yaw, length is set later
                Vector3D v = new Vector3D(Math.toRadians(this.location.getYaw()), Math.toRadians(this.location.getPitch())).normalize();

                Vector3D rot3d = new Vector3D(vec3Rot.getX(),vec3Rot.getY(),vec3Rot.getZ());
                // make v the size of length and add vec3scale
                Vector3D v1 = v.scalarMultiply(getMaxLengthPercent(l))
                        .add(new Vector3D(vec3Scale.getX(),vec3Scale.getY(),vec3Scale.getZ()));
                for (int i = 0; i < lasers.size(); i++) {
                    LaserWrapper laser = lasers.get(i);
                    /*
                    Here we make a ray the size of (length) from the location of this Light, then we add a 2d plane to it (which is where our pattern is) with an
                    x value that is separated evenly for each laser. This pattern is then moved (as a whole) by the second pattern.
                    The pattern should also curve.
                    */
                    // x position evenly separated for each laser
                    double separated = x + (100.0 / lasers.size()) * i;

                    // rotation for first and second patterns, determines "start position" when pattern is a circle
                    Rotation r = new Rotation(v1, this.data.getPatternData().getRotation(), RotationConvention.FRAME_TRANSFORM);
                    Rotation r2 = new Rotation(v1, this.data.getSecondPatternData().getRotation(), RotationConvention.FRAME_TRANSFORM);
                    // apply first pattern (separated evenly for each laser) to our ray
                    Vector3D v2 = this.data.getPatternData().getPattern().apply(v1, separated, r, this.data.getPatternData().getPatternSizeMultiplier() * (l / 100));
                    // then apply second pattern to all lasers with the same x value
                    Vector3D v3 = this.data.getSecondPatternData().getPattern().apply(v1, x2, r2, this.data.getSecondPatternData().getPatternSizeMultiplier() * (l / 100));
                    Vector3D v4 = getData().getRingMovementData().calculateMovement(zoomTime);
                    Vector3D v5 = v1.add(v4).add(v3).add(v2);

                    Location result = this.location.clone().add(v5.getX(), v5.getZ(), v5.getY());
                    Location startResult = this.location.clone().add(v4.getX(), v4.getZ(), v4.getY());
                    loc = startResult;
                    if (this.data.isFlipStartAndEnd()) {
                        laser.setStart(result);
                        laser.setEnd(startResult);
                    } else {
                        laser.setEnd(result);
                        laser.setStart(startResult);
                    }
                    laser.setTeamColor(this.color);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        this.loc = location.clone();
        this.marker = Nightclub.getMarkerFactory().build(this.location, new Color(0, 0, 0), "", 500);
        isZoomed = false;
        buildLasers();
        isLoaded = true;
    }

    public void unload() {
        this.channel.removeListener(this);
        this.speedChannel.getChannel().removeSpeedListener(this);
        off(new Color(0x000000));
        lasers.forEach(LaserWrapper::kill);
        stop();
        isLoaded = false;
    }

    public void debug(boolean on) {
        marker.setName(on ? this.toString() : "");
        marker.setDuration(on ? 3600000 : 5000);
    }

    /**
     * Starts the movement runnable of this Light. This Light will be completely stationary if it is not started before being turned on.
     */
    public void start() {
        stop();
        startTime = System.currentTimeMillis();
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(run, 1, DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the movement runnable of this Light.
     */
    public void stop() {
        executorService.shutdownNow();
        currentGradient = null;
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
                laser = Nightclub.getLaserFactory().build(location.clone().add(v4.getX(), v4.getZ(), v4.getY()), location, -1, 256, type,true);
            } else {
                laser = Nightclub.getLaserFactory().build(location, location.clone().add(v4.getX(), v4.getZ(), v4.getY()), -1, 256, type,true);
            }
            lasers.add(laser);
        }
    }

    /**
     * Turns Light on, sets length to onLength and sets timeToFade to 0
     */
    public void on(Color color, @Nullable JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        // check if light event is referencing any light ids in the lightid list
        if (!isLoaded
                || (lightIDs != null
                && !data.getLightIDs().isEmpty()
                && StreamSupport.stream(lightIDs.spliterator(), true).noneMatch(id -> data.getLightIDs().contains(id.getAsInt()))))
            return;
        if(gradientEvent != null) {
            currentGradient = gradientEvent;
        }
            lasers.forEach(LaserWrapper::start);
            if (length < data.getOnLength() && !isOn) {
                length = data.getOnLength();
            }

        length = Math.max(data.getOnLength() * (color.getAlpha() / 255.0), 0.05);
        isOn = true;
        timeToFade = 0;
        this.duration = duration + 10;
        if(currentGradient == null) {
            this.color = color;
            marker.setLocation(loc);
            marker.setColor(this.color);
            marker.setDuration(this.duration);
            marker.start(256);
        }
    }

    public void on(Color color) {
        on(color, null,0,null);
    }

    /**
     * Turns Light off, sets length to 0.1 and sets timeToFade to 0
     */
    public void off(Color color, @Nullable JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        if (!isLoaded
                || (lightIDs != null
                && !data.getLightIDs().isEmpty()
                && StreamSupport.stream(lightIDs.spliterator(), true).noneMatch(id -> data.getLightIDs().contains(id.getAsInt()))))
            return;
        if(gradientEvent != null) {
            currentGradient = gradientEvent;
        }
        marker.stop();
        lasers.forEach(LaserWrapper::stop);
        isOn = false;
        timeToFade = 0;
    }

    public void off(Color color) {
        off(color, null,0,null);
    }

    /**
     * Flashes light in a similar way to beat saber, simulating brightness with a longer beam
     */
    public void flash(Color color, @Nullable JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        if (!isLoaded
                || (lightIDs != null
                && !data.getLightIDs().isEmpty()
                && StreamSupport.stream(lightIDs.spliterator(), true).noneMatch(id -> data.getLightIDs().contains(id.getAsInt()))))
            return;
        if(gradientEvent != null) {
            currentGradient = gradientEvent;
        }
        if (isOn) {
            length = data.getOnLength() * (color.getAlpha() / 255.0);
            length += (100 - data.getOnLength()) / 3;
            timeToFade += 3;
            lasers.forEach(LaserWrapper::changeColor);
            this.duration = duration + 10;
            if(currentGradient == null) {
                this.color = color;
                marker.setLocation(loc);
                marker.setColor(this.color);
                marker.setDuration(this.duration);
                marker.start(256);
            }
        } else {
            flashOff(color);
        }
    }

    public void flash(Color color) {
        flash(color, null,0,null);
    }

    /**
     * Flashes light in a similar way to beat saber, simulating brightness with a longer beam and then fades to black
     */
    public void flashOff(Color color, @Nullable JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        if (!isLoaded
                || (lightIDs != null
                && !data.getLightIDs().isEmpty()
                && StreamSupport.stream(lightIDs.spliterator(), true).noneMatch(id -> data.getLightIDs().contains(id.getAsInt()))))
            return;
        if(gradientEvent != null) {
            currentGradient = gradientEvent;
        }
        on(color);
        flash(color);
        this.duration = duration + 10;
        if(currentGradient == null) {
            this.color = color;
            marker.setLocation(loc);
            marker.setColor(this.color);
            marker.setDuration(this.duration);
            marker.start(256);
        }
        timeToFade = data.getTimeToFadeToBlack();
    }

    public void flashOff(Color color) {
        flashOff(color, null,0,null);
    }

    public void ringZoom() {
        if (!isLoaded) return;
        isZoomed = !isZoomed;
        marker.setLocation(loc);
        marker.start(256);
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
            x = data.getPatternData().getStartX() * 1.2 + 10;
            x2 = data.getSecondPatternData().getStartX() * 1.2 + 10;
        }
        if (this.multipliedSpeed == data.getPatternData().getSpeed() * multiplier) { // laser "reset"
            x = (x + 18) % 100;
            x2 = (x2 + 18) % 100;
        }
        if (multiplier == 0) {
            x = data.getPatternData().getStartX();
            x2 = data.getSecondPatternData().getStartX();
        }
        this.multipliedSpeed = data.getPatternData().getSpeed() * multiplier;
        this.secondaryMultipliedSpeed = data.getSecondPatternData().getSpeed() * multiplier;
        if (multipliedSpeed >= 20.0) {
            multipliedSpeed = 20.0;
            if (secondaryMultipliedSpeed >= 20.0) {
                secondaryMultipliedSpeed = 20.0;
            }
        }
        run.run();
    }

    private double getMaxLengthPercent(double length) {
        return data.getMaxLength() * length / 100.0;
    }

    public void setType(LightType type) {
        this.type = type;
        buildLasers();
    }

    public String toString() {
        return """
                name="this.name
                ", channel="this.channel
                ", speedChannel="this.speedChannel
                ", lightIDs="lightid
                """
                .replace("this.name", "" + this.name)
                .replace("this.channel", "" + this.channel)
                .replace("this.speedChannel", "" + this.speedChannel)
                .replace("lightid", "" + data.getLightIDs());
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void name(String name) {
        this.name = name;
    }

    @Override
    public void position(Location location) {
        this.loc = location;
    }

    @Override
    public void active(boolean active) {
        isEnabled = active;
    }

    @Override
    public void scale(Location vec) {
        vec3Scale = vec;
    }

    @Override
    public void rotation(Location vec) {
        vec3Rot = vec;
    }

    @Override
    public void lightID(int id) {
        data.setLightIDs(new ArrayList<>(Collections.singletonList(id)));
    }

    @Override
    public Location position() {
        return location;
    }

    @Override
    public boolean active() {
        return isEnabled;
    }

    @Override
    public Location scale() {
        return vec3Scale;
    }

    @Override
    public Location rotation() {
        return vec3Rot;
    }

    @Override
    public int lightID() {
        return data.getLightIDs().get(0);
    }

    @Override
    public void destroy() {
        unload();
    }

    @SneakyThrows
    @Override
    public List<GameObject> duplicate(int amount) {
        List<GameObject> these = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Light light = new Light(uniqueID, name, location, type, channel, speedChannel, data);
            these.add(light);
            light.load();
            light.start();
            light.on(color);
        }
        return these;
    }
}
