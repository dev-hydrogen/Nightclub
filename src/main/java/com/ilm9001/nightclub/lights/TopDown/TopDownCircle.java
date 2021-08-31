package com.ilm9001.nightclub.lights.TopDown;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.Circler;
import com.ilm9001.nightclub.lights.LightAbstract;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;

public class TopDownCircle extends LightAbstract {
    Circler c;
    double angleSeperation;
    
    public TopDownCircle(Location anchor, int num_lsr) {
        this(anchor,num_lsr,true);
    }
    public TopDownCircle(Location anchor, int num_lsr,boolean rotation) {
        super(anchor,num_lsr);
        lsr.forEach((LaserWrapper lsr) -> lsr.setEnd(anchor));
        c = new Circler(0,3,rotation);
        angleSeperation = 360.0/num_lsr;
        TDCRun run = new TDCRun();
        run.runTaskTimerAsynchronously(Nightclub.getInstance(),20,2);
    }
    
    @Override
    public void setSpeed(int multiplier) {
        c.setSpeed(c.getInitialSpeed()*multiplier);
    }
    
    class TDCRun extends Run {
        public void lights() {
            double a_seperated = c.getDegrees();
            for (LaserWrapper lsr : lsr) {
                Vector3D v1 = new Vector3D(Math.toRadians(a_seperated), 0).normalize().scalarMultiply(len/1.5);
                lsr.setStart(anchor.clone().add(v1.getX(), v1.getZ() - len, v1.getY()));
                a_seperated += angleSeperation;
            }
        }
    }
}
