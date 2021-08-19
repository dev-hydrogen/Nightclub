package com.ilm9001.nightclub.lights;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.TopDown.TopDownCircle;
import com.ilm9001.nightclub.util.LaserWrapper;
import com.ilm9001.nightclub.util.Util;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class LightAbstract {
    public List<LaserWrapper> lsr;
    public Location anchor;
    public double len;
    public double len_on;
    public double len_min;
    public double len_max;
    public int off_c;
    public boolean isBlue;
    public boolean isRunning;
    
    public LightAbstract(Location anchor, int num_lsr) {
        lsr = new ArrayList<>();
        this.anchor = anchor;
        for(int i=0; i < num_lsr; i++) {
            lsr.add(new LaserWrapper(anchor, anchor, -1, 128));
        }
        len = 0;
        FileConfiguration conf = Nightclub.getInstance().getConfig();
        len_on = conf.getDouble("length_on");
        len_min = 0.0;
        len_max = conf.getDouble("length_max");
        isRunning = false;
    }
    public void setRunning(boolean bool) {
        isRunning = bool;
    }
    
    public void off() {
        if(isRunning) {
            for (LaserWrapper lsr : lsr) {
                lsr.stop();
                Nightclub.getInstance().getLogger().info("off " + lsr.hashCode());
            }
            len = 0;
            off_c = 0;
        }
    }
    public void on() {
        if(isRunning) {
            for (LaserWrapper lsr : lsr) {
                lsr.start();
                Nightclub.getInstance().getLogger().info("on " + lsr.hashCode());
            }
            len = len_on;
            off_c = 0;
        }
    }
    public void flash() {
        if(isRunning) {
            if (lsr.get(0).isStarted() || len > 0) {
                if (len < len_on) {
                    len = len_on;
                }
                len += (len_max - len_on) / 7;
                off_c = 0;
                for (LaserWrapper lsr : lsr) {
                    lsr.colorChange();
                }
            } else on();
        }
    }
    public void flashOff() {
        if(isRunning) {
            flash();
            off_c = 22;
        }
    }
    public void setColor(boolean isBlue) {
        if(isRunning) {
            this.isBlue = isBlue;
        }
    }
    
    public abstract class Run extends BukkitRunnable {
        @Override
        public void run() {
            if (off_c > 0) {
                off_c -= 1;
                len -= len_on / 20;
            }
            if (len > len_max) {
                len = len_max;
            }
            if (len < len_min || len < 0.9) {
                len = len_min;
                off_c = 1;
                if(isRunning) off();
            }
    
        }
    }
}
