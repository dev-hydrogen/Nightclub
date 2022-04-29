package exposed.hydrogen.nightclub.light;

import com.google.gson.JsonArray;

import java.awt.*;

public interface LightI {
    void on(Color color, JsonArray lightIDs);

    void off(Color color, JsonArray lightIDs);

    void flash(Color color, JsonArray lightIDs);

    void flashOff(Color color, JsonArray lightIDs);

    void start();

    void stop();

    void load();

    void unload();

    void debug(boolean on);
}
