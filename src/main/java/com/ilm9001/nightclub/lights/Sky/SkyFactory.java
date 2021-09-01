package com.ilm9001.nightclub.lights.Sky;

import com.ilm9001.nightclub.Nightclub;
import org.apache.commons.math3.util.Precision;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkyFactory {
    private Map<Integer,Sky> skyList;
    private int stepCount;
    private int step;
    
    public SkyFactory(int stepCount) {
        skyList = new HashMap<>();
        
        this.stepCount = stepCount;
        step = 256 / stepCount;
        for (int r = 0; r < 256; r += step) {
            for (int g = 0; g < 256; g += step) {
                for (int b = 0; b < 256; b += step) {
                    Nightclub.getInstance().getLogger().info("" + r + "" + g + "" + b);
                    skyList.put(new Color(r,g,b).getRGB(),new Sky(r, g, b));
                }
            }
        }
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
        return getFromRGB(new Color((r+step/2) & 0xF0, (g+step/2) & 0xF0, (b+step/2) & 0xF0).getRGB());
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
}
