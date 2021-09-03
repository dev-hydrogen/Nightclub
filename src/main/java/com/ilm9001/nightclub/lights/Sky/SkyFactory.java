package com.ilm9001.nightclub.lights.Sky;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.Circler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkyFactory {
    private ArrayList<Sky> skyList;
    private HueRunnable run;
    private Circler c;
    private int stepCount;
    private int step;
    private int n_colors;
    private double mask; // thats what the mask is
    
    public SkyFactory(int stepCount) {
        skyList = new ArrayList<>();
        c = new Circler(0,9);
        this.stepCount = stepCount;
        this.step = 256 / stepCount;
        ArrayList<Integer> colorlist = new ArrayList();
        for (int i = 0; i < 256; i += step) {
            colorlist.add(i);
        }
        colorlist.add(255);
        this.n_colors = colorlist.size();
        
        mask = 256 - step;
        for (Integer r: colorlist) {
            for (Integer g: colorlist) {
                for (Integer b: colorlist) {
                    Nightclub.getInstance().getLogger().info("r" + r + "g" + g + "b" + b);
                    skyList.add(new Sky(r,g,b));
                }
            }
        }
        //run = new HueRunnable();
        //run.runTaskTimerAsynchronously(Nightclub.getInstance(),0,4);
    }
    
    /**
     * Input any R G B value and this will format it correctly and return the closest (Rounded) Sky instance!
     *
     * @param r Red channel
     * @param g Green channel
     * @param b Blue channel
     * @return Sky instance with correct rounded spacing
     */
    
    public Sky getFromRGB(int r, int g, int b) {
        int color_index = color_idx(r)*(n_colors*n_colors) + color_idx(g)*n_colors + color_idx(b);
        return this.skyList.get(color_index);
    }
    
    private int color_idx(int c) {
        c += this.step / 2; // for rounding
        if (c >= 256) return this.n_colors-1; // maximum index
        return c / this.step;
    }

    public int getStep() {
        return step;
    }
    public int getStepCount() {
        return stepCount;
    }
    
    class HueRunnable extends BukkitRunnable {
        @Override
        public void run() {
            int clr = Color.HSBtoRGB((float)c.getDegrees()/360,1.0f,1.0f);
            SkyHandler.setSkyForAllPlayers(clr);
        }
    }
}
