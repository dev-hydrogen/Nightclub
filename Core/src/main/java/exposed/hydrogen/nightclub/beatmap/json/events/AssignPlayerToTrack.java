package exposed.hydrogen.nightclub.beatmap.json.events;

import com.google.gson.JsonObject;
import exposed.hydrogen.nightclub.beatmap.json.CustomEvent;

public class AssignPlayerToTrack extends CustomEvent<AssignPlayerToTrack.Data> {
    public AssignPlayerToTrack(JsonObject object, double bpm) {
        super(object, bpm);
        data = new Data(object.getAsJsonObject("_data"));
    }
    @lombok.Data
    public static final class Data {
        final String _track;

        Data(JsonObject object) {
            _track = object.get("_track").getAsString();
        }
    }
}
