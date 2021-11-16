package com.ilm9001.nightclub.light.event;

import com.ilm9001.nightclub.light.Light;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LightEventHandler {
    private final List<Light> lights;
    private final List<Light> speedListeners;
    
    public LightEventHandler() {
        lights = new ArrayList<>();
        speedListeners = new ArrayList<>();
    }
    
    public void addListener(Light light) {
        lights.add(light);
    }
    public void removeListener(Light light) {
        lights.remove(light);
    }
    public void addSpeedListener(Light light) {
        speedListeners.add(light);
    }
    public void removeSpeedListener(Light light) {
        speedListeners.remove(light);
    }
    
    public void on(Color color) {
        lights.forEach(l -> l.on(color));
    }
    public void off(Color color) {
        lights.forEach(Light::off);
    }
    public void flash(Color color) {
        lights.forEach(l -> l.flash(color));
    }
    public void flashOff(Color color) {
        lights.forEach(l -> l.flashOff(color));
    }
    public void start() {
        lights.forEach(Light::start);
    }
    public void stop() {
        lights.forEach(Light::stop);
    }
    public void setSpeed(double multiplier) {
        lights.forEach(l -> l.setSpeed(multiplier));
        speedListeners.forEach(l -> l.setSpeed(multiplier));
    }
}
