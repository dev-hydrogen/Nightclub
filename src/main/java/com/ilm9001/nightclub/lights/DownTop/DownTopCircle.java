package com.ilm9001.nightclub.lights.DownTop;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.Circler;
import com.ilm9001.nightclub.lights.Directions;
import com.ilm9001.nightclub.lights.LightAbstract;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;

/**
 * Flipped TopDownCircle. disgusting repeated code!
 *
 */

public class DownTopCircle extends LightAbstract {
    Circler c;
    double angleSeperation;
    
    public DownTopCircle(Location anchor, int num_lsr) {
        this(anchor,num_lsr,true);
    }
    public DownTopCircle(Location anchor, int num_lsr, boolean rotation) {
        super(anchor,num_lsr);
        if(rotation) c = LightAbstract.cN;
        else c = LightAbstract.cNAC;
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
                Vector3D v1 = new Vector3D((Directions.SOUTH.getValue() * a_seperated) / 360.0, 0).normalize().scalarMultiply(len/1.5);
                lsr.setEnd(anchor.clone().add(v1.getX(), v1.getZ() + len, v1.getY()));
                a_seperated += angleSeperation;
            }
        }
    }
}
