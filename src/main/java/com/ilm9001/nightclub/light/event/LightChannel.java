package com.ilm9001.nightclub.light.event;

import lombok.Getter;

public enum LightChannel {
    BACK_LASERS(0, new LightEventHandler()),
    RING_LIGHTS(1, new LightEventHandler()),
    LEFT_ROTATING_LASERS(2, new LightEventHandler()),
    RIGHT_ROTATING_LASERS(3, new LightEventHandler()),
    CENTER_LIGHTS(4, new LightEventHandler());
    
    @Getter private final int type;
    @Getter private final LightEventHandler handler;
    LightChannel(int type, LightEventHandler handler) {
        this.type = type;
        this.handler = handler;
    }
}
