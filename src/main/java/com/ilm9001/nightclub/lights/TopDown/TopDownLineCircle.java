package com.ilm9001.nightclub.lights.TopDown;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.Circler;
import com.ilm9001.nightclub.lights.LightAbstract;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;

public class TopDownLineCircle extends LightAbstract {
    Circler c;
    Circler c2;
    double angleSeperation;
    
    public TopDownLineCircle(Location anchor, int num_lsr) {
        super(anchor,num_lsr);
        c = new Circler(0,2);
        c2 = new Circler(18,4);
        angleSeperation = 360.0/num_lsr;
        TDDCRun run = new TDDCRun();
        run.runTaskTimerAsynchronously(Nightclub.getInstance(),20,2);
    }
    
    @Override
    public void setSpeed(int multiplier) {
        c.setSpeed(c.getInitialSpeed()*multiplier);
        c2.setSpeed(c2.getInitialSpeed()*multiplier);
    }
    
    class TDDCRun extends Run {
        public void lights() {
            double a_seperated = c.getDegrees();
            double b = c2.getDegrees();
            for (LaserWrapper lsr : lsr) {
                Vector3D v1 = new Vector3D((Nightclub.getDirection().getDivisibleByDegrees() * a_seperated) / 360.0, 0).normalize().scalarMultiply(len/1.3);
                Vector3D v2 = new Vector3D(0,(2.0 * Math.PI * b) / 360.0).normalize().scalarMultiply(len*1.08);
                Location firstiteration = anchor.clone().add(v2.getX(), 0.0-len, v2.getY());
                lsr.setEnd(firstiteration.clone().add(v1.getX(), v1.getZ(), v1.getY()));
                a_seperated += angleSeperation;
            }
        }
    }
}
