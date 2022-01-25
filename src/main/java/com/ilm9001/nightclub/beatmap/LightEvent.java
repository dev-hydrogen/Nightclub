package com.ilm9001.nightclub.beatmap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * A singular Light Event from a parsed beatmap file.
 * https://bsmg.wiki/mapping/map-format.html
 */
public final class LightEvent {
    @Getter private final Integer type;
    @Getter private final Integer value;
    @Getter private final Long time;
    @Getter private final Color color;
    @Getter @Nullable private final JsonObject customData;
    @Getter @Nullable private final JsonArray lightID;
    @Getter private final boolean isChroma;
    
    public LightEvent(JsonObject event, double bpm, boolean isChroma, InfoData info) {
        type = event.get("_type").getAsInt();
        value = event.get("_value").getAsInt();
        time = Math.round(event.get("_time").getAsDouble() * 1000.0 * 1000.0 * 60.0 / bpm); // THIS IS MICROSECONDS!
        customData = (JsonObject) event.get("_customData");
        this.isChroma = isChroma;
        if (isChroma && customData != null && customData.get("_color") != null) {
            lightID = customData.get("_lightID") != null ? customData.get("_lightID").getAsJsonArray() : null;
            JsonArray color = customData.get("_color").getAsJsonArray();
            float r = color.get(0).getAsFloat();
            float g = color.get(1).getAsFloat();
            float b = color.get(2).getAsFloat();
            float a = 1.0F;
            if (color.size() >= 4 && color.get(3) != null) {
                a = color.get(3).getAsFloat();
            }
            float divisor = Math.max(Math.max(r, g), Math.max(Math.max(b, a), 1F)); // Beat saber color system is utter fucking dogshit.
            this.color = new Color(r / divisor, g / divisor, b / divisor, a / divisor);
        } else {
            lightID = null;
            if (value < 4 && value != 0) {
                color = info.getPrimaryColor();
            } else if (value > 4) {
                color = info.getSecondaryColor();
            } else {
                color = new Color(0x000000);
            }
        }
    }
    
    public LightEvent(int type, int value, long time, Color color, boolean isChroma) {
        this.type = type;
        this.value = value;
        this.time = time;
        this.color = color;
        this.isChroma = isChroma;
        this.customData = null;
        this.lightID = null;
    }
}
