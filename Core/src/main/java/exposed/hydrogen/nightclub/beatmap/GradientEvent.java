package exposed.hydrogen.nightclub.beatmap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import exposed.hydrogen.nightclub.util.Easing;
import exposed.hydrogen.nightclub.util.Lerp;
import exposed.hydrogen.nightclub.util.Util;
import lombok.Getter;

import java.awt.*;
import java.util.Locale;

public final class GradientEvent {
    @Getter private final Long startTime;
    @Getter private final Long endTime;
    @Getter private final double duration;
    @Getter private final Color startColor;
    @Getter private final Color endColor;
    @Getter private final Easing easing;
    @Getter private final Lerp lerpType;

    public GradientEvent(JsonElement json, Long startTime, double bpm) {
        JsonObject object = json.getAsJsonObject();
        this.startTime = startTime;
        duration = object.get("_duration").getAsDouble();
        endTime = startTime + Math.round(duration * 1000.0 * 60.0 / bpm);
        startColor = Util.translateBeatSaberColor(object.get("_startColor").getAsJsonArray());
        endColor = Util.translateBeatSaberColor(object.get("_endColor").getAsJsonArray());
        easing = object.get("_easing") != null
                ? Easing.valueOf(object.get("_easing").getAsString()) :
                Easing.easeLinear;
        lerpType = object.get("_lerpType") != null
                ? Lerp.valueOf(object.get("_lerpType").getAsString().toUpperCase(Locale.ROOT))
                : Lerp.RGB;
    }
}
