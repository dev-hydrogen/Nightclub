package com.ilm9001.nightclub.laser;

import com.ilm9001.nightclub.Nightclub;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class LaserWrapper {
    @Getter private Laser laser;
    @Getter private Location start;
    @Getter private Location end;
    @Getter private int duration;
    @Getter private int distance;
    @Getter private Laser.LaserType type;
    
    
    public LaserWrapper(Location start, Location end, int duration, int distance, Laser.LaserType type) {
        try {
            laser = type.create(start,end,duration,distance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return;
        }
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.distance = distance;
        this.type = type;
    }
    
    public void start() {
        if(!laser.isStarted()) {laser.start(Nightclub.getInstance());}
    }
    
    public void stop() {
        if(laser.isStarted()) {laser.stop();}
    }
    
    public void setStart(Location start) {
        this.start = start;
        try {
            laser.moveStart(start);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    
    public void setEnd(Location end) {
        this.end = end;
        try {
            laser.moveEnd(end);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
