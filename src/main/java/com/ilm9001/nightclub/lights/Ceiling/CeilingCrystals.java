package com.ilm9001.nightclub.lights.Ceiling;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.LightAbstract;
import com.ilm9001.nightclub.util.Laser;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;

/**
 * Unused.
 */

public class CeilingCrystals extends LightAbstract {
    private static final double Offset = Nightclub.getInstance().getConfig().getDouble("CeilingOffset");
    
    public CeilingCrystals(Location anchor, int num_lght) {
        super(anchor,num_lght, Laser.LaserType.GUARDIAN,
                Nightclub.getDirection().getZ()*Offset,0,
                Nightclub.getDirection().getX()*Offset); //Switching out Z and X causes 90deg spawning rotation which is what we want
        TDCRun run = new TDCRun();
        run.runTaskTimerAsynchronously(Nightclub.getInstance(),20,2);
    }
    
    @Override
    public void setSpeed(int multiplier) {}
    
    class TDCRun extends Run {
        public void lights() {
            for (LaserWrapper lsr : lsr) {
                Vector3D v1 = new Vector3D(Nightclub.getDirection().getRadians()+Math.toRadians(90), 0).normalize().scalarMultiply(len*6);
                lsr.setEnd(anchor.clone().add(v1.getX(), v1.getZ(), v1.getY()));
            }
        }
    }
}
