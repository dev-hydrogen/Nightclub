package com.ilm9001.nightclub.lights;

import com.ilm9001.nightclub.Nightclub;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ScheduledExecutorService;

public class Circler {
    private double a;
    private ScheduledExecutorService sch;
    private int speed = 3;
    private final int initialSpeed;
    private boolean rotateClockWise;
    private CirclerRunnable runnable;
    
    public Circler() {
        this(0,3,true);
    }
    public Circler(int offset) {
        this(offset,3,true);
    }
    public Circler(int offset, int speed) {
        this(offset,speed,true);
    }
    public Circler(int offset, int speed, boolean rotateClockWise) {
        runnable = new CirclerRunnable();
        runnable.runTaskTimerAsynchronously(Nightclub.getInstance(),0,2);
        a = offset;
        this.speed = speed;
        initialSpeed = speed;
        this.rotateClockWise = rotateClockWise;
    }
    
    public double getDegrees() {
        return a;
    }
    public double getRadians() {
        return Math.toRadians(a);
    }
    public Vector3D getVector(double length) {
        return new Vector3D(getRadians(),0).normalize().scalarMultiply(length);
    }
    public void setRunning(boolean bool) {
        if(bool && runnable.isCancelled()) {
            runnable.runTaskTimerAsynchronously(Nightclub.getInstance(),0,2);
        } else if (!bool && !runnable.isCancelled()) {
            runnable.cancel();
        }
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        if(this.speed == speed) {
            if(!rotateClockWise) {
                a-=speed*1.5; // perform "laser reset" if the speed is the same as before
            } else {
                a+=speed*1.5;
            }
        }
        this.speed = speed;
    }
    public boolean isRunning() {
        return !runnable.isCancelled();
    }
    public int getInitialSpeed() {
        return initialSpeed;
    }
    public void setRotateClockWise(boolean bool) {
        rotateClockWise = bool;
    }
    public void flipDirection() {
        rotateClockWise = !rotateClockWise;
    }
    
    class CirclerRunnable extends BukkitRunnable {
        @Override
        public void run() {
            if (!rotateClockWise) {
                a = a % -360 - speed;
            } else {
                a = a % 360 + speed;
            }
        }
    }
}
