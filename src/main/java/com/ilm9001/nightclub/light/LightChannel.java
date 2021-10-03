package com.ilm9001.nightclub.light;

import lombok.Getter;

public enum LightChannel {
    BACK_LASERS(0),RING_LIGHTS(1),LEFT_ROTATING_LASERS(2),RIGHT_ROTATING_LASERS(3),CENTER_LIGHTS(4);
    
    @Getter private final int type;
    LightChannel(int type) {
        this.type = type;
    }
}
