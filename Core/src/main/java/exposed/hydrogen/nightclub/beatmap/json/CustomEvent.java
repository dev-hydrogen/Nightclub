package exposed.hydrogen.nightclub.beatmap.json;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public abstract class CustomEvent<Data> {
    private long time;
    private String type;
    protected Data data;

    public CustomEvent(JsonObject object, double bpm) {
        time = Math.round(object.get("_time").getAsDouble() * 1000.0 * 1000.0 * 60.0 / bpm);
        type = object.get("_type").getAsString();
    }
}
