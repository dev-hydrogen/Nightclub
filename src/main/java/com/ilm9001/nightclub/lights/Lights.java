package com.ilm9001.nightclub.lights;

import com.ilm9001.nightclub.lights.TopDown.TopDownCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownDoubleCircle;
import com.ilm9001.nightclub.util.Util;

public enum Lights {
    TOPDOWN_CIRCLE (new TopDownCircle(Util.getLocFromConf("TopDownCircle"),4)),
    TOPDOWNDOUBLE_CIRCLE (new TopDownDoubleCircle(Util.getLocFromConf("TopDownDoubleCircle"),4));
    
    LightAbstract light;
    Lights(LightAbstract light) {
        this.light = light;
    }
    
    public LightAbstract getLight() {
        return light;
    }
}
