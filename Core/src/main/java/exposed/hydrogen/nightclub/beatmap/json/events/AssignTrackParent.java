package exposed.hydrogen.nightclub.beatmap.json.events;

import com.google.gson.JsonObject;
import exposed.hydrogen.nightclub.beatmap.json.CustomEvent;

import java.util.ArrayList;
import java.util.List;

public class AssignTrackParent extends CustomEvent<AssignTrackParent.Data> {

    public AssignTrackParent(JsonObject object) {
        super(object);
        data = new Data(object.getAsJsonObject("_data"));
    }
    @lombok.Data
    class Data {
        List<String> _childrenTracks;
        String _parentTrack;
        boolean _worldPositionStays;

        Data(JsonObject object) {
            var arr = object.getAsJsonArray("_childrenTracks");
            _childrenTracks = new ArrayList<>();
            arr.forEach(str -> _childrenTracks.add(str.getAsString()));
            _parentTrack = object.get("_parentTrack").getAsString();
            _worldPositionStays = object.get("_worldPositionStays") != null && object.get("_worldPositionStays").getAsBoolean();
        }
    }
}
