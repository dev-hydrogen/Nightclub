package com.ilm9001.nightclub.laser;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.Location;
import lombok.Getter;

public class LaserWrapper {
    @Getter private Laser laser;
    @Getter private Location start;
    @Getter private Location end;
    @Getter private int duration;
    @Getter private int distance;
    @Getter private Laser.LaserType type;
    private volatile boolean isStarted;
    
    
    public LaserWrapper(Location start, Location end, int duration, int distance, Laser.LaserType type) {
        this.start = start;
        this.end = end;
        try {
            laser = type.create(this.start.getBukkitLocation(), this.end.getBukkitLocation(), duration, distance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return;
        }
        this.duration = duration;
        this.distance = distance;
        this.type = type;
    }
    
    public synchronized void start() {
        if (!isStarted) {
            laser.start(Nightclub.getInstance());
        }
        isStarted = true;
    }
    
    public synchronized void stop() {
        if (isStarted) {
            laser.stop();
        }
        isStarted = false;
    }
    
    public void setStart(Location start) {
        this.start = start;
        try {
            laser.moveStart(start.getBukkitLocation());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    
    public void setEnd(Location end) {
        this.end = end;
        try {
            laser.moveEnd(end.getBukkitLocation());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    public void changeColor() {
        if (type == Laser.LaserType.GUARDIAN) {
            try {
                ((Laser.GuardianLaser) laser).callColorChange();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
}
