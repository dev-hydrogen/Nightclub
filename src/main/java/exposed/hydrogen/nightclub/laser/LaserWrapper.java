package exposed.hydrogen.nightclub.laser;

import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.light.LightType;
import exposed.hydrogen.nightclub.util.Location;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class LaserWrapper {
    @Getter private Laser laser;
    @Getter private Location start;
    @Getter private Location end;
    @Getter private int duration;
    @Getter private int distance;
    @Getter private Laser.LaserType type;
    private volatile boolean isStarted;


    public LaserWrapper(Location start, Location end, int duration, int distance, LightType type) {
        this.start = start;
        this.end = end;
        try {
            laser = type.getType().create(this.start.getBukkitLocation(), this.end.getBukkitLocation(), duration, distance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return;
        }
        this.duration = duration;
        this.distance = distance;
        this.type = type.getType();
    }

    public synchronized void start() {
        if (!isStarted) {
            laser.start(Nightclub.getInstance());
        }
        isStarted = true;
    }

    public synchronized void stop() {
        if (isStarted) {
            laser.stop();
        }
        isStarted = false;
    }

    public void setStart(@NotNull Location start) {
        this.start = start;
        try {
            laser.moveStart(this.start.getBukkitLocation());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void setEnd(@NotNull Location end) {
        this.end = end;
        try {
            laser.moveEnd(this.end.getBukkitLocation());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void changeColor() {
        if (!(laser instanceof Laser.GuardianLaser)) {
            return;
        }
        try {
            ((Laser.GuardianLaser) laser).callColorChange();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
