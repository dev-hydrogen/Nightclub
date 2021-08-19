package com.ilm9001.nightclub.lights.TopDown;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.Circler;
import com.ilm9001.nightclub.lights.LightAbstract;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class TopDownCircle extends LightAbstract {
    Circler c;
    double angleSeperation;
    
    public TopDownCircle(Location anchor, int num_lsr) {
        super(anchor,num_lsr);
        c = new Circler(0,9);
        angleSeperation = 360.0/num_lsr;
        TDCRun run = new TDCRun();
        run.runTaskTimerAsynchronously(Nightclub.getInstance(),20,2);
    }
    class TDCRun extends Run {
        @Override
        public void run() {
            double a_seperated = c.getDegrees();
            for (LaserWrapper lsr : lsr) {
                Vector3D v1 = new Vector3D((2.0 * Math.PI * a_seperated) / 360.0, 0).normalize().scalarMultiply(len/1.5);
                lsr.setEnd(anchor.clone().add(v1.getX(), v1.getZ() - len, v1.getY()));
                a_seperated += angleSeperation;
            }
           
        }
    }
}
