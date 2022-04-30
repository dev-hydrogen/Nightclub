package exposed.hydrogen.nightclub.wrapper;

import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.Location;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class DebugMarkerWrapper {
    @Getter protected Location location;
    protected Color color;
    protected String name;
    protected int duration;
    protected int distanceSquared;
    protected ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public DebugMarkerWrapper(Location location, Color color, String name, int duration) {
        this.location = location;
        this.duration = duration;
    }

    public abstract void start(int distance);

    public abstract void start(int distance, Runnable callback);

    public abstract void stop();

    public abstract void stopAll(int distance);

    public abstract void stopAll(Location location, int distance);

    public abstract void stopAll(List<CrossCompatPlayer> players);

    public abstract void setData(Location location, Color color, String name, int duration);

    public void setLocation(Location location) {
        this.location = location;
        setData(this.location, this.color, this.name, this.duration);
    }

    public void setColor(Color color) {
        this.color = color;
        setData(this.location, this.color, this.name, this.duration);
    }

    public void setName(String name) {
        this.name = name;
        setData(this.location, this.color, this.name, this.duration);
    }

    public void setDuration(int duration) {
        this.duration = duration;
        setData(this.location, this.color, this.name, this.duration);
    }

    public Location getLocation() {
        return location;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    protected boolean isCloseEnough(Location location) {
        return distanceSquared == -1 ||
                this.location.distanceSquared(location) <= distanceSquared;
    }
    protected boolean isCloseEnough(Location location1, Location location2) {
        return distanceSquared == -1 ||
                location1.distanceSquared(location2) <= distanceSquared;
    }

    enum LaserType {
        GUARDIAN,
        ENDER_CRYSTAL
    }
}
