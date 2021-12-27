package com.ilm9001.nightclub.light;

import java.awt.*;

public interface LightI {
    void on(Color color);
    
    void off(Color color);
    
    void flash(Color color);
    
    void flashOff(Color color);
    
    void start();
    
    void stop();
    
    void load();
    
    void unload();
}
