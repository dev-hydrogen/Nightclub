package com.ilm9001.nightclub.util;

import com.ilm9001.nightclub.Nightclub;
import org.bukkit.Location;

import java.util.concurrent.locks.ReentrantLock;

public class LaserWrapper {
    private Laser.GuardianLaser laser;
    private volatile boolean stopped;
    private Location start;
    private Location end;
    private int time;
    private int seeDistance;
    private ReentrantLock mutex;
    
    public LaserWrapper(Location start, Location end, int time, int seeDistance) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.seeDistance = seeDistance;
        stopped = true;
        mutex = new ReentrantLock();
        try {
            laser = new Laser.GuardianLaser(start,end,time,seeDistance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized boolean start() {
        try {
            mutex.lock();
            if (stopped) {
                laser.start(Nightclub.getInstance());
                laser.callColorChange();
                stopped = false;
                return true;
            } else return false;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        } finally {
            mutex.unlock();
        }
    }
    
    public synchronized void stop() {
        try {
            mutex.lock();
            if (stopped) {
                return;
            }
            stopped = true;
            laser.stop();
        } finally {
            mutex.unlock();
        }
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
    
    public synchronized void colorChange() {
        if(!stopped) {
            try {laser.callColorChange();}
            catch (ReflectiveOperationException e) {e.printStackTrace();}
        }
    }
    
    public boolean isStarted() {
        return !stopped;
    }
    
    public Laser.GuardianLaser getLaser() {
        return laser;
    }
}
