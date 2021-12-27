package com.ilm9001.nightclub.light.event;

import com.ilm9001.nightclub.light.Light;
import com.ilm9001.nightclub.light.LightI;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public enum LightChannel {
    BACK_LASERS(0),
    RING_LIGHTS(1),
    LEFT_ROTATING_LASERS(2),
    RIGHT_ROTATING_LASERS(3),
    CENTER_LIGHTS(4);
    
    @Getter private final int type;
    private final List<LightI> lights;
    private final List<LightI> speedListeners;
    LightChannel(int type) {
        this.type = type;
        lights = new ArrayList<>();
        speedListeners = new ArrayList<>();
    }
    public void addListener(LightI light) {
        lights.add(light);
    }
    public void removeListener(LightI light) {
        lights.remove(light);
    }
    public void addSpeedListener(LightI light) {
        speedListeners.add(light);
    }
    public void removeSpeedListener(LightI light) {
        speedListeners.remove(light);
    }
    
    public void on(Color color) {
        lights.forEach(l -> l.on(color));
    }
    public void off(Color color) {
        lights.forEach(l -> l.off(color));
    }
    public void flash(Color color) {
        lights.forEach(l -> l.flash(color));
    }
    public void flashOff(Color color) {
        lights.forEach(l -> l.flashOff(color));
    }
    public void start() {
        lights.forEach(LightI::start);
    }
    public void stop() {
        lights.forEach(LightI::stop);
    }
    public void setSpeed(double multiplier) {
        lights.forEach(l -> {
            if (l instanceof Light) {
                ((Light) l).setSpeed(multiplier);
            }
        });
        speedListeners.forEach(l -> {
            if (l instanceof Light) {
                ((Light) l).setSpeed(multiplier);
            }
        });
    }
}
