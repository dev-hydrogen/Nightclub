package exposed.hydrogen.nightclub;

import co.aikar.commands.MinestomCommandManager;
import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.minestom.MinestomChameleon;
import exposed.hydrogen.nightclub.wrapper.DebugMarkerFactory;
import exposed.hydrogen.nightclub.wrapper.LaserFactory;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;

public class NightclubMinestom extends Extension {
    @Getter private static NightclubMinestom instance;
    @Getter private static MinestomUtil util;
    @Getter private static MinestomCommandManager commandManager;
    @Getter @Setter private static Instance mapInstance;
    @Getter private static Team noCollisionTeam;
    private MinestomChameleon chameleon;

    @Override
    public LoadStatus initialize() {
        noCollisionTeam = new TeamBuilder("NoCollision",MinecraftServer.getTeamManager())
                .collisionRule(TeamsPacket.CollisionRule.PUSH_OTHER_TEAMS)
                .build();

        instance = this;
        util = new MinestomUtil();
        Nightclub.setCrossCompatUtil(util);
        Nightclub.setLaserFactory(new LaserFactory<LaserMinestom>(LaserMinestom.class));
        Nightclub.setMarkerFactory(new DebugMarkerFactory<DebugMarkerMinestom>(DebugMarkerMinestom.class));
        mapInstance = (Instance) MinecraftServer.getInstanceManager().getInstances().toArray()[0]; // BAD but i dont care
        try {
            chameleon = new MinestomChameleon(Nightclub.class, this, Nightclub.getPluginData());
            chameleon.onEnable();
        } catch (ChameleonInstantiationException ex) {
            ex.printStackTrace();
            return LoadStatus.FAILED;
        }
        // needs to be after chameleon
        commandManager = new MinestomCommandManager();
        Nightclub.registerCommands(commandManager);
        Nightclub.registerCompletions(commandManager.getCommandCompletions());
        return LoadStatus.SUCCESS;
    }

    @Override
    public void terminate() {
        Nightclub.getLightUniverseManager().save();
        chameleon.onDisable();
    }
}
