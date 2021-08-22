package com.ilm9001.nightclub.lights;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.LaserWrapper;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class LightAbstract {
    public List<LaserWrapper> lsr;
    public Location anchor;
    public double len;
    public double len_on;
    public double len_min;
    public double len_max;
    public int off_c;
    public boolean isBlue;
    public ReentrantLock mutex;
    
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
        mutex = new ReentrantLock();
    }
    
    public synchronized void off() {
        try {
            mutex.lock();
            for (LaserWrapper lsr : lsr) {
                lsr.stop();
            }
            len = 0;
            off_c = 0;
        } finally {
            mutex.unlock();
        }
    }
    public synchronized void on() {
        try {
            mutex.lock();
            for (LaserWrapper lsr : lsr) {
                lsr.start();
            }
            len = len_on;
            off_c = 0;
        } finally {
             mutex.unlock();
        }
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
                }
            }
            lights();
        }
        public abstract void lights();
    }
}
