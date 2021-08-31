package com.ilm9001.nightclub.lights.Center;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.LightAbstract;
import com.ilm9001.nightclub.lights.Ring.RingSquare;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Cube extends LightAbstract {
    private Map<RingSquare,List<LaserWrapper>> linker;
    private List<RingSquare> rings;
    private Location anchor;
    private ShowRunnable runnable;
    
    public Cube(Location anchor, double cubeSize) {
        super(anchor,12);
        linker = new HashMap<>();
        rings = new ArrayList<>();
        this.anchor = anchor;
        for (int i = 0; i <= 4;) {
            RingSquare square = new RingSquare(cubeSize, anchor.clone().add(Nightclub.getDirection().getX()*i,0,Nightclub.getDirection().getZ()*i));
            rings.add(square);
            linker.put(square, lsr.subList(i, i + 3));
            i+=4;
        }
        runnable = new ShowRunnable();
        runnable.runTaskTimerAsynchronously(Nightclub.getInstance(),20,2);
    }
    
    @Override
    public void setSpeed(int multiplier) {
        for(RingSquare ring : rings) {
            ring.setSpeed(3*multiplier);
        }
    }
    
    
    class ShowRunnable extends Run {
        @Override
        public void lights() {
            int iteration = 0;
            for(RingSquare ring : rings) {
                ring.setCenter(anchor.clone().add(Nightclub.getDirection().getX()*len*iteration,0,Nightclub.getDirection().getZ()*len*iteration));
                ring.setSize(len);
                List<Location> points = ring.getPoints();
                AtomicInteger i= new AtomicInteger();
                linker.get(ring).forEach((LaserWrapper lsr) -> {
                    lsr.setStart(points.get(i.get()));
                    i.getAndIncrement();
                    lsr.setEnd(points.get(i.get()));
                    i.getAndIncrement();
                });
                iteration++;
            }
            for (int i = 8; i < lsr.size(); i++) {
                List<Location> loc1 = rings.get(0).getPoints();
                List<Location> loc2 = rings.get(1).getPoints();
                lsr.get(i).setStart(loc1.get(i-8));
                lsr.get(i).setEnd(loc2.get(i-8));
            }
        }
    }
}