package com.ilm9001.nightclub.lights.Ring;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RingSquare {
    private Location center;
    private RotationRunnable runnable;
    private double size;
    private double r; // rotation
    private double c; // counter
    private boolean running;
    
    public RingSquare(double size, Location center) {
        this.size = size;
        runnable = new RotationRunnable();
        running = false;
        this.center = center;
        r=45;
    }
    
    public void on() {
        if(!running) runnable.runTaskTimerAsynchronously(Nightclub.getInstance(),0,2); // What the fuck is wrong with me?
        running = true;
    }
    public void off() {
        if(running) runnable.cancel();
        running = false;
    }
    
    public void setCenter(Location center) {this.center = center;}
    public Location getCenter() {return center.clone();}
    
    public List<Location> getPoints() {
        List<Location> list = new ArrayList<>();
    
        for (int i = 0; i <= 5; i++) {
            Vector3D v1 = new Vector3D(Nightclub.getDirection().getValue(),Math.toRadians(r+i*90)).normalize().scalarMultiply(size);
            list.add(center.clone().add(v1.getX(),v1.getZ(),v1.getY()));
        }
        return list;
    }
    
    public void rotate() {
        rotate(10);
    }
    public void rotate(double degrees) {
        c += degrees;
        if(c > 115) {
            c = 115;
        }
    }
    
    class RotationRunnable extends BukkitRunnable {
        @Override
        public void run() {
            if(c > 0) {
                r = r + (c/1.3) % 360;
                c -= 1+c/20;
            }
        }
    }
}
