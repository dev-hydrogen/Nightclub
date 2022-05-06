package exposed.hydrogen.nightclub;

import co.aikar.commands.CommandIssuer;
import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.CrossCompatUtil;
import exposed.hydrogen.nightclub.util.Location;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MinestomUtil implements CrossCompatUtil {
    public MinestomUtil() {
    }

    @Override
    public List<CrossCompatPlayer> getListOfPlayers() {
        Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
        List<CrossCompatPlayer> list = new LinkedList<>();
        for (Player player : players) {
            list.add(new MinestomPlayer(player));
        }
        return list;
    }

    @Override
    public @Nullable CrossCompatPlayer getPlayer(CommandIssuer commandIssuer) {
        if (!commandIssuer.isPlayer()) return null;
        return new MinestomPlayer(commandIssuer.getIssuer());
    }

    public static Location getNightclubLocation(Pos pos) {
        return new Location(pos.x(), pos.y(), pos.z(),
                -pos.pitch(), pos.yaw() - 270);
    }

    public static Pos getMinestomPos(exposed.hydrogen.nightclub.util.Location location) {
        return new Pos(location.getX(), location.getY(), location.getZ(),
                (float) location.getYaw() + 270, (float) -location.getPitch());
    }
}
