package com.ilm9001.nightclub.lights.Sky;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.Circler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SkyFactory {
    private Map<Integer,Sky> skyList;
    private HueRunnable run;
    private Circler c;
    private int stepCount;
    private int step;
    private double mask; // thats what the mask is

    public SkyFactory(int stepCount) {
        skyList = new HashMap<>();
        c = new Circler(0,9);
        this.stepCount = stepCount;
        step = 256 / stepCount;
        mask = 256 - step;
        for (int r = 0; r <= 255; r += step) {
            for (int g = 0; g <= 255; g += step) {
                for (int b = 0; b <= 255; b += step) {
                    Nightclub.getInstance().getLogger().info("r" + r + "g" + g + "b" + b);
                    skyList.put(new Color(r,g,b).getRGB(),new Sky(r, g, b));
                }
            }
        }
        run = new HueRunnable();
        run.runTaskTimerAsynchronously(Nightclub.getInstance(),0,4);
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
        return getFromRGB(new Color(r - r % step,g - g % step,b - b % step).getRGB());
    }
    private Sky getFromRGB(int rgb) {
        return skyList.get(rgb);
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
