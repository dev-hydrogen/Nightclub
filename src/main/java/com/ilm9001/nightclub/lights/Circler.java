package com.ilm9001.nightclub.lights;

import com.ilm9001.nightclub.util.Util;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Circler {
    private int a;
    private boolean isRunning;
    private ScheduledExecutorService sch;
    private int speed = 3;
    private final int initialSpeed;
    private boolean rotateClockWise;
    
    public Circler() {
        sch = Executors.newScheduledThreadPool(1);
        sch.schedule(new CirclerRunnable(),0, TimeUnit.MILLISECONDS);
        isRunning = true;
        a = 0;
        initialSpeed = speed;
        rotateClockWise = true;
    }
    public Circler(int offset) {
        sch = Executors.newScheduledThreadPool(1);
        sch.schedule(new CirclerRunnable(),0, TimeUnit.MILLISECONDS);
        isRunning = true;
        a = offset;
        initialSpeed = speed;
        rotateClockWise = true;
    }
    public Circler(int offset, int speed) {
        sch = Executors.newScheduledThreadPool(1);
        sch.schedule(new CirclerRunnable(),0, TimeUnit.MILLISECONDS);
        isRunning = true;
        a = offset;
        this.speed = speed;
        initialSpeed = speed;
        rotateClockWise = true;
    }
    public Circler(int offset, int speed, boolean rotateClockWise) {
        sch = Executors.newScheduledThreadPool(1);
        sch.schedule(new CirclerRunnable(),0, TimeUnit.MILLISECONDS);
        isRunning = true;
        a = offset;
        this.speed = speed;
        initialSpeed = speed;
        this.rotateClockWise = rotateClockWise;
    }
    
    public int getDegrees() {
        return a;
    }
    public double getPied() {
        return (2.0 * Math.PI * a) / 360.0;
    }
    public Vector3D getVector(double length) {
        return new Vector3D(getPied(),0).normalize().scalarMultiply(length);
    }
    public void setRunning(boolean b) {
        if(b && !isRunning) sch.schedule(new CirclerRunnable(),0, TimeUnit.MILLISECONDS);
        isRunning = b;
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public boolean isRunning() {
        return isRunning;
    }
    public int getInitialSpeed() {
        return initialSpeed;
    }
    public void setRotateClockWise(boolean tf) {
        rotateClockWise = tf;
    }
    public void flipDirection() {
        rotateClockWise = !rotateClockWise;
    }
    
    class CirclerRunnable implements Runnable {
        @Override
        public void run() {
            while(isRunning) {
                if (!rotateClockWise) {
                    a = a % -360 - speed;
                } else {
                    a = a % 360 + speed;
                }
                Util.safe_sleep(100);
            }
        }
    }
}
