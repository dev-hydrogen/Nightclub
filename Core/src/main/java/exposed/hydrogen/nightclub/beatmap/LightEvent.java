package exposed.hydrogen.nightclub.beatmap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import exposed.hydrogen.nightclub.util.Util;
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
    @Getter @Nullable private final GradientEvent gradientEvent;
    @Getter @Nullable private final JsonArray lightID;
    @Getter private final Number speed;
    @Getter private final boolean isChroma;

    public LightEvent(JsonObject event, double bpm, boolean isChroma, InfoData info) {
        type = event.get("_type").getAsInt();
        value = event.get("_value").getAsInt();
        time = Math.round(event.get("_time").getAsDouble() * 1000.0 * 1000.0 * 60.0 / bpm); // THIS IS MICROSECONDS!
        customData = (JsonObject) event.get("_customData");
        this.isChroma = isChroma;
        if(isChroma && customData != null) {
            lightID = customData.get("_lightID") != null
                    ? customData.get("_lightID") instanceof JsonArray
                    ? customData.get("_lightID").getAsJsonArray()
                    : newArray(customData.get("_lightID").getAsInt())
                    : null;
            speed = customData.get("_speed") != null
                    ? customData.get("_speed").getAsNumber()
                    : customData.get("_preciseSpeed") != null
                    ? customData.get("_preciseSpeed").getAsNumber()
                    : value;
            gradientEvent = customData.get("_lightGradient") != null
                    ? new GradientEvent(customData.get("_lightGradient"),Math.round(event.get("_time").getAsDouble() * 1000.0 * 60.0 / bpm),bpm)
                    : null;
            if (customData.get("_color") != null) {
                JsonArray color = customData.get("_color").getAsJsonArray();
                this.color = Util.translateBeatSaberColor(color);
            } else {
                color = getColorFromValue(value, info);
            }
        } else {
            speed = value;
            lightID = null;
            color = getColorFromValue(value, info);
            gradientEvent = null;
        }
    }

    private Color getColorFromValue(int value, InfoData info) {
        if (value < 4 && value != 0) {
            return info.getPrimaryColor();
        } else if (value > 4) {
            return info.getSecondaryColor();
        } else {
            return new Color(0x000000);
        }
    }

    private JsonArray newArray(int i) {
        JsonArray array = new JsonArray();
        array.add(i);
        return array;
    }
}
