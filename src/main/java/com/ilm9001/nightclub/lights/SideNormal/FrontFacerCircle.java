package com.ilm9001.nightclub.lights.SideNormal;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.Circler;
import com.ilm9001.nightclub.lights.LightAbstract;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;

public class FrontFacerCircle extends LightAbstract {
    Circler c;
    double angleSeperation;
    
    public FrontFacerCircle(Location anchor, int num_lsr) {
        this(anchor,num_lsr,true);
    }
    public FrontFacerCircle(Location anchor, int num_lsr, boolean rotation) {
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
                Vector3D v1 = new Vector3D(0, Math.toRadians(a_seperated)).normalize().scalarMultiply(len/2.6);
                Vector3D v2 = new Vector3D(Nightclub.getDirection().getValue()+Math.toRadians(90),0).normalize().scalarMultiply(len*2.7);
                lsr.setEnd(anchor.clone().add(v2.getX(), v2.getZ(), v2.getY()).add(v1.getX(),v1.getZ(),v1.getY()));
                a_seperated += angleSeperation;
            }
        }
    }
}
