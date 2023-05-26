package exposed.hydrogen.nightclub.beatmap.json.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import exposed.hydrogen.nightclub.beatmap.json.ColorPointDefinition;
import exposed.hydrogen.nightclub.beatmap.json.CustomEvent;
import exposed.hydrogen.nightclub.beatmap.json.TriplePointDefinition;
import exposed.hydrogen.nightclub.util.Easing;

import java.util.List;

import static exposed.hydrogen.nightclub.beatmap.json.TriplePointDefinition.fromArray;

public class AnimateTrack extends CustomEvent<AnimateTrack.Data> {

    public AnimateTrack(JsonObject object, double bpm) {
        super(object, bpm);
        data = new Data(object.getAsJsonObject("_data"), bpm);
    }
    @lombok.Data
    public static final class Data {
        final List<String> _track;
        final double _duration;
        final Easing _easing;
        final List<TriplePointDefinition> _position;
        final List<TriplePointDefinition> _localPosition;
        final List<TriplePointDefinition> _rotation;
        final List<TriplePointDefinition> _localRotation;
        final List<TriplePointDefinition> _scale;
        final List<ColorPointDefinition> _color;
        Data(JsonObject object, double bpm) {
            JsonElement track = object.get("_track");
            if(track.isJsonPrimitive()) {
                _track = List.of(track.getAsString());
            } else {
                var arr = track.getAsJsonArray();
                _track = new java.util.ArrayList<>();
                arr.forEach(str -> _track.add(str.getAsString()));
            }
            double duration = object.get("_duration") != null ? object.get("_duration").getAsDouble() : 0;
            _duration = Math.round(duration * 1000.0 * 1000.0 * 60.0 / bpm);
            _easing = object.get("_easing") != null ? Easing.valueOf(object.get("_easing").getAsString()) : Easing.easeLinear;
            _position = fromArray(object.get("_position"));
            _localPosition = fromArray(object.get("_localPosition"));
            _rotation = fromArray(object.get("_rotation"));
            _localRotation = fromArray(object.get("_localRotation"));
            _scale = fromArray(object.get("_scale"));
            _color = ColorPointDefinition.fromArray(object.get("_position"));
        }
    }
}
