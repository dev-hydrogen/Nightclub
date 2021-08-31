package com.ilm9001.nightclub.lights.Ring;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rings {
    private Map<RingSquare,List<LaserWrapper>> ringLinker;
    private List<RingSquare> rings;
    private List<LaserWrapper> lasers;
    private Location anchor;
    private ShowRunnable runnable;
    private double ringSeperation;
    private boolean running;
    private boolean zoom;
    
    public Rings(Location anchor, int ringCount, double ringSize, double ringSeperation) {
        ringLinker = new HashMap<>();
        rings = new ArrayList<>();
        lasers = new ArrayList<>();
        this.anchor = anchor;
        this.ringSeperation = ringSeperation;
        zoom = false;
        for (int i = ringCount; i > 0; i--) {
            rings.add(new RingSquare(ringSize-i/10.0,anchor.clone().subtract(Nightclub.getDirection().getX()*ringSeperation*i,0,Nightclub.getDirection().getZ()*ringSeperation*i)));
        }
        for(RingSquare ring : rings) {
            List<LaserWrapper> ringlasers = new ArrayList<>();
            List<Location> points = ring.getPoints();
            for (int i = 0; i < points.size()-2; i++) {
                LaserWrapper lsr = new LaserWrapper(points.get(i),points.get(i+1),-1,128);
                ringlasers.add(lsr);
                lasers.add(lsr);
                // I think this is pretty clever.
            }
            ringLinker.put(ring,ringlasers);
        }
        runnable = null;
        running = false;
    }
    
    public void spin() {
        for (int i = 0; i <= rings.size()-1; i++) {
            rings.get(i).rotate((i*0.8)+6);
        }
    }
    
    public void zoom() {
        if(!zoom) {
            for (RingSquare ring : rings) {
                ring.setCenter(ring.getCenter().subtract(Nightclub.getDirection().getX() * ringSeperation, 0, Nightclub.getDirection().getZ() * ringSeperation));
            }
        } else {
            for (RingSquare ring : rings) {
                ring.setCenter(ring.getCenter().add(Nightclub.getDirection().getX() * ringSeperation, 0, Nightclub.getDirection().getZ() * ringSeperation));
            }
        }
        zoom = !zoom;
    }
    
    public void on() {
        if(!running) {
            for (LaserWrapper lsr : lasers) {
                if (!lsr.isStarted()) lsr.start();
            }
            for(RingSquare ring : rings) {
                ring.on();
            }
            if(runnable == null) {
                runnable = new ShowRunnable();
                runnable.runTaskTimerAsynchronously(Nightclub.getInstance(), 0, 2);
            } else Nightclub.getInstance().getLogger().info("Something has gone wrong! A new runnable is being started even though it is not null!");
        }
        running = true;
    }
    public void off() {
        if(running) {
            for (LaserWrapper lsr : lasers) {
                if (lsr.isStarted()) lsr.stop();
            }
            for(RingSquare ring : rings) {
                ring.off();
            }
            runnable.cancel();
            runnable = null;
        }
        running = false;
    }
    class ShowRunnable extends BukkitRunnable {
        @Override
        public void run() {
            for(RingSquare ring : rings) {
                List<LaserWrapper> lsrs = ringLinker.get(ring);
                List<Location> locs = ring.getPoints();
                for (int i = 0; i < locs.size() - 2; i++) {
                    lsrs.get(i).setStart(locs.get(i));
                    lsrs.get(i).setEnd(locs.get(i+1));
                }
            }
        }
    }
}
