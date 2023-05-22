package exposed.hydrogen.nightclub.beatmap.json.events;

import com.google.gson.JsonObject;
import exposed.hydrogen.nightclub.beatmap.json.ColorPointDefinition;
import exposed.hydrogen.nightclub.beatmap.json.CustomEvent;
import exposed.hydrogen.nightclub.beatmap.json.TriplePointDefinition;
import exposed.hydrogen.nightclub.util.Easing;

import java.util.List;

import static exposed.hydrogen.nightclub.beatmap.json.TriplePointDefinition.fromArray;

public class AnimateTrack extends CustomEvent<AnimateTrack.Data> {

    public AnimateTrack(JsonObject object) {
        super(object);
        data = new Data(object.getAsJsonObject("_data"));
    }
    @lombok.Data
    class Data {
        String _track;
        double _duration;
        Easing _easing;
        List<TriplePointDefinition> _position;
        List<TriplePointDefinition> _rotation;
        List<TriplePointDefinition> _localRotation;
        List<TriplePointDefinition> _scale;
        List<ColorPointDefinition> _color;
        Data(JsonObject object) {
            _track = object.get("_track").getAsString();
            _duration = object.get("_duration") != null ? object.get("_duration").getAsDouble() : 0;
            _easing = object.get("_easing") != null ? Easing.valueOf(object.get("_easing").getAsString()) : Easing.easeLinear;
            _position = fromArray(object.get("_position"));
            _rotation = fromArray(object.get("_rotation"));
            _localRotation = fromArray(object.get("_localRotation"));
            _scale = fromArray(object.get("_scale"));
            _color = ColorPointDefinition.fromArray(object.get("_position"));
        }
    }
}
