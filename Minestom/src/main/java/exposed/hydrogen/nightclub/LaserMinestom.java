package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.monster.GuardianMeta;
import net.minestom.server.entity.metadata.water.SquidMeta;
import org.jetbrains.annotations.NotNull;

public class LaserMinestom extends LaserWrapper {
    private final Entity guardian;
    private final Entity squid;
    private final GuardianMeta guardianMeta;
    private SquidMeta squidMeta;

    public LaserMinestom(Location start, Location end, int duration, int distance, LightType type) {
        super(start, end, duration, distance, type);
        guardian = new Entity(EntityType.GUARDIAN);
        squid = new Entity(EntityType.SQUID);
        guardianMeta = (GuardianMeta) guardian.getEntityMeta();
        squidMeta = (SquidMeta) squid.getEntityMeta();
        guardianMeta.setTarget(squid);
        setData(guardian);
        setData(squid);
    }

    @Override
    public void start() {
        guardian.spawn();
        squid.spawn();
    }

    @Override
    public void stop() {
        guardian.remove();
        squid.remove();
    }

    @Override
    public void setStart(@NotNull Location start) {
        this.start = start;
        guardian.teleport(MinestomUtil.getMinestomPos(this.start));
    }

    @Override
    public void setEnd(@NotNull Location end) {
        this.end = end;
        squid.teleport(MinestomUtil.getMinestomPos(this.end));
    }

    @Override
    public void changeColor() {
        guardianMeta.setTarget(null);
        guardianMeta.setTarget(squid);
    }

    private void setData(Entity entity) {
        entity.setNoGravity(true);
        entity.setInvisible(true);
        entity.setSilent(true);
    }
}
