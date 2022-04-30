package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import org.jetbrains.annotations.NotNull;

public class LaserMinestom extends LaserWrapper {
    public LaserMinestom(Location start, Location end, int duration, int distance, LightType type) {
        super(start, end, duration, distance, type);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setStart(@NotNull Location start) {

    }

    @Override
    public void setEnd(@NotNull Location end) {

    }

    @Override
    public void changeColor() {

    }
}
