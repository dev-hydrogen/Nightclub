package exposed.hydrogen.nightclub.Laser;

import exposed.hydrogen.nightclub.NightclubSpigot;
import exposed.hydrogen.nightclub.SpigotUtil;
import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LaserTranslator extends LaserWrapper {
    @Getter private Laser laser;
    public LaserTranslator(Location start, Location end, Integer duration, Integer distance, LightType type, Boolean glow) {
        super(start, end, duration, distance, type,glow);
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
        if(start.equals(this.start)) {
            return;
        }
        this.start = start;
        try {
            laser.moveStart(SpigotUtil.getBukkitLocation(start));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void setEnd(@NotNull Location end) {
        if(end.equals(this.end)) {
            return;
        }
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

    @Override
    public void setTeamColor(Color color) {

    }

    public void kill() {

    }
}
