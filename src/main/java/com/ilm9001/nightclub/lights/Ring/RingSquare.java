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
    
    public RingSquare(double size, Location center) {
        this.size = size;
        runnable = new RotationRunnable();
        runnable.runTaskTimerAsynchronously(Nightclub.getInstance(),0,2);
        this.center = center;
        r=45;
    }
    
    public void setCenter(Location center) {this.center = center;}
    public Location getCenter() {return center.clone();}
    
    public List<Location> getPoints() {
        List<Location> list = new ArrayList<>();
    
        for (int i = 0; i <= 5; i++) {
            Vector3D v1 = new Vector3D(0,Math.toRadians(r+i*90)).normalize().scalarMultiply(size);
            list.add(center.clone().add(v1.getX(),v1.getZ(),v1.getY()));
        }
        return list;
    }
    
    public void rotate() {
        rotate(10);
    }
    public void rotate(double degrees) {
        c += degrees;
    }
    
    class RotationRunnable extends BukkitRunnable {
        @Override
        public void run() {
            if(c > 0) {
                r += 1+c/10;
                c -= 2+c/10;
            }
        }
    }
}
