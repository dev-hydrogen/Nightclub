package com.ilm9001.nightclub.lights.TopDown;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.Circler;
import com.ilm9001.nightclub.lights.LightAbstract;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class TopDownDoubleCircle extends LightAbstract {
    Circler c;
    Circler c2;
    double angleSeperation;
    
    public TopDownDoubleCircle(Location anchor, int num_lsr) {
        super(anchor,num_lsr);
        c = new Circler(0,15);
        c2 = new Circler(5,30);
        angleSeperation = 360.0/num_lsr;
        TDDCRun run = new TDDCRun();
        run.runTaskTimerAsynchronously(Nightclub.getInstance(),20,2);
    }
    class TDDCRun extends Run {
        @Override
        public void run() {
            double a_seperated = c.getDegrees();
            double b_seperated = c2.getDegrees();
            for (LaserWrapper lsr : lsr) {
                Vector3D v1 = new Vector3D((2.0 * Math.PI * a_seperated) / 360.0, 0).normalize().scalarMultiply(len/1.5);
                Vector3D v2 = new Vector3D((2.0 * Math.PI * b_seperated) / 360.0,0).normalize().scalarMultiply(len/3);
                lsr.setEnd(anchor.clone().add(v1.getX(), v1.getZ() - len, v1.getY()).add(v2.getX(), v2.getZ(), v2.getY()));
                a_seperated += angleSeperation;
            }
            
        }
    }
}

