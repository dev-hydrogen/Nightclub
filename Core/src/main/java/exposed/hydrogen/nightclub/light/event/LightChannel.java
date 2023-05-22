package exposed.hydrogen.nightclub.light.event;

import com.google.gson.JsonArray;
import exposed.hydrogen.nightclub.beatmap.json.GradientEvent;
import exposed.hydrogen.nightclub.light.Light;
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
    private final List<Light> lights;
    private final List<Light> speedListeners;
    @Getter private boolean debugOn;

    LightChannel(int type) {
        this.type = type;
        lights = new ArrayList<>();
        speedListeners = new ArrayList<>();
    }

    public void initializePlayback() {
        start();
        off(new Color(0, 0, 0), null,0,null);
        reset();
    }

    public void terminatePlayback() {
        off(new Color(0, 0, 0), null,0,null);
        stop();
        reset();
    }

    public void reset() {
        lights.forEach(light -> {
            light.setX(light.getData().getPatternData().getStartX());
            light.setX2(light.getData().getSecondPatternData().getStartX());
            light.setSpeed(1);
        });
    }

    public void addListener(Light light) {
        lights.add(light);
    }

    public void removeListener(Light light) {
        lights.remove(light);
    }

    public void addSpeedListener(Light light) {
        speedListeners.add(light);
    }

    public void removeSpeedListener(Light light) {
        speedListeners.remove(light);
    }

    public void on(Color color, @Nullable JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        lights.forEach(l -> l.on(color, lightIDs,duration,gradientEvent));
    }

    public void off(Color color, @Nullable JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        lights.forEach(l -> l.off(color, lightIDs,duration,gradientEvent));
    }

    public void flash(Color color, @Nullable JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        lights.forEach(l -> l.flash(color, lightIDs,duration,gradientEvent));
    }

    public void flashOff(Color color, @Nullable JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        lights.forEach(l -> l.flashOff(color, lightIDs,duration,gradientEvent));
    }

    public void start() {
        lights.forEach(Light::start);
    }

    public void stop() {
        lights.forEach(Light::stop);
    }

    public void setSpeed(double multiplier) {
        lights.forEach(l -> {
            l.setSpeed(multiplier);
        });
        speedListeners.forEach(l -> {
            l.setSpeed(multiplier);
        });
    }

    public void debug(boolean on) {
        debugOn = on;
        lights.forEach(l -> l.debug(on));
    }
}
