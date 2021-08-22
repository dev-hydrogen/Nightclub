package com.ilm9001.nightclub.lights;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.Laser;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class LightAbstract {
    //In the name of "performance".
    public static final Circler cN = new Circler(0,3,true);
    public static final Circler cO = new Circler(180,3,true);
    public static final Circler cNAC = new Circler(0,3,false);
    public static final Circler cOAC = new Circler(180,3,false);
    
    public List<LaserWrapper> lsr;
    public Location anchor;
    public double len;
    public double len_on;
    public double len_min;
    public double len_max;
    public int off_c;
    public boolean isBlue;
    
    public LightAbstract(Location anchor, int num_lsr) {
        this(anchor,num_lsr, Laser.LaserType.GUARDIAN,0,0,0);
    }
    public LightAbstract(Location anchor, int num_lsr, Laser.LaserType type) {
        this(anchor,num_lsr,type,0,0,0);
    }
    public LightAbstract(Location anchor, int num_lsr, Laser.LaserType type,double oX,double oY,double oZ) {
        lsr = new ArrayList<>();
        this.anchor = anchor;
        for(int i=0; i < num_lsr; i++) {
            lsr.add(new LaserWrapper(anchor.clone().add(i*oX,i*oY,i*oZ), anchor, -1, 128,type));
        }
        len = 0;
        FileConfiguration conf = Nightclub.getInstance().getConfig();
        len_on = conf.getDouble("length_on");
        len_min = 0.0;
        len_max = conf.getDouble("length_max");
    }
    
    public void off() {
        for (LaserWrapper lsr : lsr) {
            lsr.stop();
        }
        len = 0;
        off_c = 0;
    }
    public void on() {
        for (LaserWrapper lsr : lsr) {
            lsr.start();
        }
        len = len_on;
        off_c = 0;
    }
    public void flash() {
        if (lsr.get(0).isStarted() || len > 0) {
            if (len < len_on) {
                len = len_on;
            }
            len += (len_max - len_on) / 10;
            off_c = 1;
            for (LaserWrapper lsr : lsr) {
                lsr.colorChange();
            }
        } else on();
    }
    public void flashOff() {
        flash();
        off_c = 32;
    }
    public void setColor(boolean isBlue) {
        this.isBlue = isBlue;
    }
    
    public abstract void setSpeed(int multiplier);
    
    public abstract class Run extends BukkitRunnable {
        @Override
        public void run() {
            if(lsr.get(0).isStarted()) {
                if (off_c > 0) {
                    off_c -= 1;
                    len -= len_on / 30;
                }
                if (len > len_max) {
                    len = len_max;
                }
                if (len <= len_min) {
                    len = len_min;
                    off_c = 1;
                    off();
                    len = len_on;
                }
            }
            lights();
        }
        public abstract void lights();
    }
}
