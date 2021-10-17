package com.ilm9001.nightclub.beatmap;

import com.google.gson.JsonObject;
import lombok.Getter;

public class LightEvent {
    @Getter private final Integer type;
    @Getter private final Integer value;
    @Getter private final Long time;
    
    public LightEvent(JsonObject event, double bpm) {
        this.type = event.get("_type").getAsInt();
        this.value = event.get("_value").getAsInt();
        time = Math.round(event.get("_time").getAsDouble() * 1000.0 * 1000.0 * 60.0 / bpm); // THIS IS MICROSECONDS!
    }
}
