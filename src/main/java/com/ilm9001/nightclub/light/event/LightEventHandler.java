package com.ilm9001.nightclub.light.event;

import com.ilm9001.nightclub.light.Light;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LightEventHandler {
    private final List<Light> lights;
    
    public LightEventHandler() {
        lights = new ArrayList<>();
    }
    
    public void addListener(Light light) {
        lights.add(light);
    }
    public void removeListener(Light light) {
        lights.remove(light);
    }
    
    public void on(Color color) {
        lights.forEach(Light::on);
    }
    public void off(Color color) {
        lights.forEach(Light::off);
    }
    public void flash(Color color) {
        lights.forEach(Light::flash);
    }
    public void flashOff(Color color) {
        lights.forEach(Light::flashOff);
    }
    public void setSpeed(double multiplier) {
        lights.forEach(l -> l.setSpeed(multiplier));
    }
}
