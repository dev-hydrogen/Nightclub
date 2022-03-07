package exposed.hydrogen.nightclub.light.event;

import com.google.gson.JsonArray;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.LightI;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public enum LightChannel {
    BACK_LASERS(0),
    RING_LIGHTS(1),
    LEFT_ROTATING_LASERS(2),
    RIGHT_ROTATING_LASERS(3),
    CENTER_LIGHTS(4);

    @Getter private final int type;
    private final List<LightI> lights;
    private final List<LightI> speedListeners;

    LightChannel(int type) {
        this.type = type;
        lights = new ArrayList<>();
        speedListeners = new ArrayList<>();
    }

    public void initializePlayback() {
        start();
        off(new Color(0, 0, 0), null);
        reset();
    }

    public void terminatePlayback() {
        off(new Color(0, 0, 0), null);
        stop();
        reset();
    }

    public void reset() {
        lights.forEach(light -> {
            if (light instanceof Light) {
                ((Light) light).setX(((Light) light).getData().getPatternData().getStartX());
                ((Light) light).setX2(((Light) light).getData().getSecondPatternData().getStartX());
                ((Light) light).setSpeed(1);
            }
        });
    }

    public void addListener(LightI light) {
        lights.add(light);
    }

    public void removeListener(LightI light) {
        lights.remove(light);
    }

    public void addSpeedListener(LightI light) {
        speedListeners.add(light);
    }

    public void removeSpeedListener(LightI light) {
        speedListeners.remove(light);
    }

    public void on(Color color, @Nullable JsonArray lightIDs) {
        lights.forEach(l -> l.on(color, lightIDs));
    }

    public void off(Color color, @Nullable JsonArray lightIDs) {
        lights.forEach(l -> l.off(color, lightIDs));
    }

    public void flash(Color color, @Nullable JsonArray lightIDs) {
        lights.forEach(l -> l.flash(color, lightIDs));
    }

    public void flashOff(Color color, @Nullable JsonArray lightIDs) {
        lights.forEach(l -> l.flashOff(color, lightIDs));
    }

    public void start() {
        lights.forEach(LightI::start);
    }

    public void stop() {
        lights.forEach(LightI::stop);
    }

    public void setSpeed(double multiplier) {
        lights.forEach(l -> {
            if (l instanceof Light) {
                ((Light) l).setSpeed(multiplier);
            }
        });
        speedListeners.forEach(l -> {
            if (l instanceof Light) {
                ((Light) l).setSpeed(multiplier);
            }
        });
    }
}
