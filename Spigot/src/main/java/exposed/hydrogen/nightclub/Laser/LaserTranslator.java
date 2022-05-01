package exposed.hydrogen.nightclub.laser;

import exposed.hydrogen.nightclub.NightclubSpigot;
import exposed.hydrogen.nightclub.SpigotUtil;
import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class LaserTranslator extends LaserWrapper {
    @Getter private Laser laser;


    public LaserTranslator(Location start, Location end, Integer duration, Integer distance, LightType type) {
        super(start, end, duration, distance, type);
        try {
            Laser.LaserType laserType = type == LightType.GUARDIAN_BEAM ? Laser.LaserType.GUARDIAN : Laser.LaserType.ENDER_CRYSTAL;
            laser = laserType.create(SpigotUtil.getBukkitLocation(start), SpigotUtil.getBukkitLocation(end), duration, distance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public synchronized void start() {
        if (!isStarted) {
            laser.start(NightclubSpigot.getInstance());
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
            laser.moveStart(SpigotUtil.getBukkitLocation(start));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void setEnd(@NotNull Location end) {
        this.end = end;
        try {
            laser.moveEnd(SpigotUtil.getBukkitLocation(end));
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
