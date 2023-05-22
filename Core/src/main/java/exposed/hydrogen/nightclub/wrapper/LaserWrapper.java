package exposed.hydrogen.nightclub.wrapper;

import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class LaserWrapper {
    @Getter protected Location start;
    @Getter protected Location end;
    @Getter protected final int duration;
    @Getter protected final int distance;
    @Getter protected final LightType type;
    @Getter protected Color color;
    protected volatile boolean isStarted;


    public LaserWrapper(Location start, Location end, int duration, int distance, LightType type, boolean glow) {
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.distance = distance;
        this.type = type;
    }

    public abstract void start();

    public abstract void stop();

    public abstract void setStart(@NotNull Location start);

    public abstract void setEnd(@NotNull Location end);

    public abstract void changeColor();

    public abstract void setTeamColor(Color color);

    public abstract void kill();
}
