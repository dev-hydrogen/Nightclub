package com.ilm9001.nightclub.util;

import com.ilm9001.nightclub.Nightclub;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LaserWrapper {
    private Laser laser;
    private boolean stopped;
    private Location start;
    private Location end;
    private int time;
    private int seeDistance;
    
    public LaserWrapper(Location start, Location end, int time, int seeDistance) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.seeDistance = seeDistance;
        stopped = false;
        try {
            laser = new Laser(start, end, time, seeDistance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    
    public boolean start() {
        if(stopped) {
            //laser.start(Nightclub.getInstance());
            try { laser.stop(); laser = new Laser(start, end, time, seeDistance); }
            catch (ReflectiveOperationException e) { e.printStackTrace(); return false; }
        } else return false;
        stopped = false;
        laser.start(Nightclub.getInstance());
        return true;
    }
    
    public void stop() {
        if(stopped) return;
        stopped = true;
        laser.stop();
    }
    
    public void setStart(Location start) {
        this.start = start;
        if(!stopped) {
            try {laser.moveStart(start);}
            catch (ReflectiveOperationException e) {e.printStackTrace();}
        }
    }
    
    public void setEnd(Location end) {
        this.end = end;
        if(!stopped) {
            try {laser.moveEnd(end);}
            catch (ReflectiveOperationException e) {e.printStackTrace();}
        }
    }
    
    public void colorChange() {
        if(!stopped) {
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
