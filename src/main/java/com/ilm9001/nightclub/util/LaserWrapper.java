package com.ilm9001.nightclub.util;

import com.ilm9001.nightclub.Nightclub;
import org.bukkit.Location;

public class LaserWrapper {
    private Laser.GuardianLaser laser;
    private volatile boolean stopped;
    private Location start;
    private Location end;
    private int time;
    private int seeDistance;
    private Laser.LaserType type;
    
    public LaserWrapper(Location start, Location end, int time, int seeDistance) {
        this(start,end,time,seeDistance, Laser.LaserType.GUARDIAN);
    }
    
    public LaserWrapper(Location start, Location end, int time, int seeDistance, Laser.LaserType type) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.seeDistance = seeDistance;
        this.type = type;
        stopped = true;
        try {
            laser = new Laser.GuardianLaser(start,end,time,seeDistance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized boolean start() {
        try {
            if (stopped) {
                laser.start(Nightclub.getInstance());
                laser.callColorChange();
                stopped = false;
                return true;
            } else return false;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public synchronized void stop() {
        if (stopped) {
            return;
        }
        stopped = true;
        laser.stop();
    }
    
    public synchronized void setStart(Location start) {
        this.start = start;
        if(!stopped) {
            try {laser.moveStart(start);}
            catch (ReflectiveOperationException e) {e.printStackTrace();}
        }
    }
    
    public synchronized void setEnd(Location end) {
        this.end = end;
        if(!stopped) {
            try {laser.moveEnd(end);}
            catch (ReflectiveOperationException e) {e.printStackTrace();}
        }
    }
    
    public Location getStart() {
        return start;
    }
    
    public Location getEnd() {
        return end;
    }
    
    public synchronized void colorChange() {
        if(!stopped &&type == Laser.LaserType.GUARDIAN) {
            try {laser.callColorChange();}
            catch (ReflectiveOperationException e) {e.printStackTrace();}
        }
    }
    
    public boolean isStarted() {
        return !stopped;
    }
    
    public Laser getLaser() {
        return laser;
    }
}
