package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.monster.GuardianMeta;
import net.minestom.server.entity.metadata.other.EndCrystalMeta;
import org.jetbrains.annotations.NotNull;

public class LaserMinestom extends LaserWrapper {
    private LivingEntity guardian;
    private LivingEntity squid;
    private Entity laser;
    private EndCrystalMeta endCrystalMeta;
    private GuardianMeta guardianMeta;

    public LaserMinestom(Location start, Location end, Integer duration, Integer distance, LightType type) {
        super(start, end, duration, distance, type);
        if(this.type == LightType.GUARDIAN_BEAM) {
            guardian = new LivingEntity(EntityType.GUARDIAN);
            squid = new LivingEntity(EntityType.SQUID);
            guardianMeta = (GuardianMeta) guardian.getEntityMeta();
            guardianMeta.setTarget(squid);
            setData(guardian);
            setData(squid);
            setTeam(guardian);
            setTeam(squid);
            return;
        }
        laser = new Entity(EntityType.END_CRYSTAL);
        endCrystalMeta = (EndCrystalMeta) laser.getEntityMeta();
        endCrystalMeta.setBeamTarget(MinestomUtil.getMinestomPos(end));
        setData(laser);
    }

    @Override
    public void start() {
        if(type == LightType.GUARDIAN_BEAM) {
            guardianMeta.setTarget(squid);
            return;
        }
        endCrystalMeta.setBeamTarget(MinestomUtil.getMinestomPos(end));
    }

    @Override
    public void stop() {
        if(type == LightType.GUARDIAN_BEAM) {
            guardianMeta.setTarget(null);
            return;
        }
        endCrystalMeta.setBeamTarget(null);
    }

    @Override
    public void setStart(@NotNull Location start) {
        if(start.equals(this.start)) {
            return;
        }
        this.start = start;
        if(type == LightType.GUARDIAN_BEAM) {
            guardian.teleport(MinestomUtil.getMinestomPos(this.start));
            return;
        }
        laser.teleport(MinestomUtil.getMinestomPos(this.start));
    }

    @Override
    public void setEnd(@NotNull Location end) {
        if(end.equals(this.end)) {
            return;
        }
        this.end = end;
        if(type == LightType.GUARDIAN_BEAM) {
            squid.teleport(MinestomUtil.getMinestomPos(this.end));
            return;
        }
        endCrystalMeta.setBeamTarget(MinestomUtil.getMinestomPos(this.end));
    }

    @Override
    public void changeColor() {
        if(type == LightType.GUARDIAN_BEAM) {
            guardianMeta.setTarget(squid);
        }
    }

    private void setTeam(LivingEntity entity) {
        entity.setTeam(NightclubMinestom.getNoCollisionTeam());
    }
    private void setData(Entity entity) {
        entity.setNoGravity(true);
        entity.setInvisible(true);
        entity.setSilent(true);
        entity.setInstance(NightclubMinestom.getMapInstance());
    }
}
