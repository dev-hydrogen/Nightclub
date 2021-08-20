package com.ilm9001.nightclub.util;

import com.google.common.util.concurrent.Monitor;
import com.ilm9001.nightclub.Nightclub;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class LaserWrapper {
    private volatile Laser laser;
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
    }
    
    public synchronized boolean start() {
        try {
            mutex.lock();
            if (stopped && laser == null) {
                try {
                    laser = new Laser(start, end, time, seeDistance); // The same laser can not be stopped and restarted.
                    // Trust me, I have tried.
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                    return false;
                }
            } else return false;
            stopped = false;
            laser.start(Nightclub.getInstance());
        } finally {
            mutex.unlock();
        }
        return true;
    }
    
    public synchronized void stop() {
        try {
            mutex.lock();
            if (stopped && laser == null) {
                return;
            }
            stopped = true;
            laser.stop();
            laser = null;
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
    
    public Laser getLaser() {
        return laser;
    }
}
