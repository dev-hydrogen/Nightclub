package exposed.hydrogen.nightclub.beatmap.json;

import com.google.gson.JsonObject;

public abstract class CustomEvent<Data> {
    private double time;
    private String type;
    protected Data data;

    public CustomEvent(JsonObject object) {
        time = object.get("_time").getAsDouble();
        type = object.get("_type").getAsString();
    }
}
