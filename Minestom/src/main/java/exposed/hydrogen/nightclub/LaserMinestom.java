package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.monster.GuardianMeta;
import net.minestom.server.entity.metadata.other.EndCrystalMeta;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LaserMinestom extends LaserWrapper {
    private LivingEntity guardian;
    private LivingEntity squid;
    private Entity laser;
    private EndCrystalMeta endCrystalMeta;
    private GuardianMeta guardianMeta;
    private Team team;

    public LaserMinestom(Location start, Location end, Integer duration, Integer distance, LightType type, Boolean glow) {
        super(start, end, duration, distance, type, glow);
        this.color = new Color(0,0,0);
        if(this.type == LightType.GUARDIAN_BEAM) {
            guardian = new LivingEntity(EntityType.GUARDIAN);
            squid = new LivingEntity(EntityType.SQUID);
            guardianMeta = (GuardianMeta) guardian.getEntityMeta();
            guardianMeta.setTarget(squid);
            setData(guardian,glow);
            setData(squid,glow);
            setTeam(guardian);
            setTeam(squid);
            return;
        }
        laser = new Entity(EntityType.END_CRYSTAL);
        endCrystalMeta = (EndCrystalMeta) laser.getEntityMeta();
        endCrystalMeta.setBeamTarget(MinestomUtil.getMinestomPos(end));
        setData(laser,glow);
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
        this.start = start;
        if(type == LightType.GUARDIAN_BEAM) {
            guardian.teleport(MinestomUtil.getMinestomPos(this.start));
            return;
        }
        laser.teleport(MinestomUtil.getMinestomPos(this.start));
    }

    @Override
    public void setEnd(@NotNull Location end) {
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

    @Override
    public void setTeamColor(Color color) {
        this.color = color;
        if(this.type == LightType.END_CRYSTAL_BEAM) {
            return;
        }
        team.updateTeamColor(NamedTextColor.nearestTo(TextColor.color(this.color.getRGB())));
    }

    private void setTeam(LivingEntity entity) {
        team = new TeamBuilder(""+ this.hashCode(), MinecraftServer.getTeamManager())
                .teamColor(NamedTextColor.nearestTo(TextColor.color(color.getRGB())))
                .collisionRule(TeamsPacket.CollisionRule.NEVER)
                .build();
        entity.setTeam(team);
    }
    private void setData(Entity entity, boolean glow) {
        entity.setNoGravity(true);
        entity.setInvisible(true);
        entity.setSilent(true);
        entity.setGlowing(glow);
        entity.setInstance(NightclubMinestom.getMapInstance());
    }
}
